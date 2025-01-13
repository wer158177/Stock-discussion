package com.hangha.mvclivechatservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.mvclivechatservice.domain.entity.ChatMessage;
import com.hangha.mvclivechatservice.domain.entity.ChatRoom;
import com.hangha.mvclivechatservice.domain.repository.ChatMessageRepository;
import com.hangha.mvclivechatservice.domain.repository.ChatRoomRepository;
import com.hangha.mvclivechatservice.infrastructure.cache.ChatRoomCacheManager;
import com.hangha.mvclivechatservice.infrastructure.dto.ChatMessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final ChatRoomCacheManager chatRoomCacheManager;
    private final Queue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private static final int BATCH_SIZE = 500;



    public ChatService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository,
                       WebSocketSessionManager sessionManager, ObjectMapper objectMapper, ChatRoomCacheManager chatRoomCacheManager) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
        this.chatRoomCacheManager = chatRoomCacheManager;
    }

    /**
     * 메시지 저장
     */
    public ChatMessageResponseDto saveMessage(String roomName, UserResponseDto senderInfo, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("[ChatService] Content cannot be null or empty");
        }

        // DB에서 영속 상태의 ChatRoom 조회
        ChatRoom chatRoom = chatRoomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Chat room with name " + roomName + " not found."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomName(chatRoom.getName())
                .senderName(senderInfo.getUsername())
                .senderProfileUrl(senderInfo.getImageUrl())
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return new ChatMessageResponseDto(savedMessage);
    }



    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void scheduleFlushMessagesToDatabase() {
        flushMessagesToDatabase(); // 실제 비동기 처리 메서드를 호출
    }

    // 비동기적으로 메시지를 데이터베이스에 저장하는 메서드
    @Async
    public void flushMessagesToDatabase() {
        List<ChatMessage> batch = new ArrayList<>();
        int count = 0;

        // 큐에서 최대 BATCH_SIZE만큼 메시지를 꺼내어 저장
        while (!messageQueue.isEmpty() && count < BATCH_SIZE) {
            batch.add(messageQueue.poll());
            count++;
        }

        // 배치가 비어 있지 않으면 데이터베이스에 저장
        if (!batch.isEmpty()) {
            chatMessageRepository.saveAll(batch);
            log.info("[ChatService] {}개의 메시지를 저장했습니다.", batch.size());
        }
    }

    // 메시지를 큐에 추가하는 메서드
    public void addMessageToQueue(ChatMessage message) {
        messageQueue.add(message);
    }







    /**
     * 메시지 브로드캐스트
     */
    public void broadcastMessage(ChatRoom chatRoom, ChatMessageResponseDto responseDto, WebSocketSession senderSession) {
        String broadcastMessage;
        try {
            // 메시지 직렬화
            broadcastMessage = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            log.error("[ChatService] 메시지 직렬화 실패: {}", e.getMessage(), e);
            return; // 직렬화 실패 시 전송 중단
        }

        // 너무많이나와..
//        log.info("[ChatService] Broadcasting message to room: {}", chatRoom.getName());

        // 브로드캐스트 수행
        sessionManager.getSessions(chatRoom.getName()).forEach(session -> {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(broadcastMessage));
                    }

                } catch (IOException e) {
                    log.error("[ChatService] 메시지 전송 실패 (세션 ID: {}): {}", session.getId(), e.getMessage());
                    sessionManager.removeSession(chatRoom.getName(), session); // 닫힌 세션 정리
                }
            } else {
                log.warn("[ChatService] 닫힌 세션 감지 (세션 ID: {})", session.getId());
                sessionManager.removeSession(chatRoom.getName(), session); // 닫힌 세션 정리
            }

        });
    }

    /**
     * 유저를 채팅방에 추가 (고정된 방만 허용)
     */
    public ChatRoom addUserToRoom(WebSocketSession session, UserResponseDto userInfo, String roomName) {
        if (roomName == null || userInfo == null) {
            log.error("[ChatService] roomName 또는 userInfo가 null입니다. roomName: {}, userInfo: {}", roomName, userInfo);
            throw new IllegalArgumentException("roomName or userInfo cannot be null");
        }

        ChatRoom chatRoom = getChatRoomByName(roomName);

        // null 확인 및 기본값 설정
        String imageUrl = userInfo.getImageUrl() != null ? userInfo.getImageUrl() : "default_image_url";

        session.getAttributes().put("username", userInfo.getUsername());
        session.getAttributes().put("imageUrl", imageUrl);
        session.getAttributes().put("roomName", roomName);

        sessionManager.addSession(roomName, session);

        return chatRoom;
    }

    /**
     * WebSocket 세션을 통해 유저를 채팅방에서 제거
     */
    public void removeUserFromRoom(WebSocketSession session) {
        String roomName = (String) session.getAttributes().get("roomName");

        if (roomName == null) {
            return; // 종료
        }

        sessionManager.removeSession(roomName, session);
    }

    public ChatRoom getChatRoomByName(String roomName) {
        // 1) 캐시 조회
        ChatRoom cached = chatRoomCacheManager.getCachedChatRoom(roomName);
        if (cached != null) {
            return cached;
        }

        // 2) DB 조회
        ChatRoom chatRoom = chatRoomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Chat room with name " + roomName + " not found."));

        // 3) 캐시에 저장
        chatRoomCacheManager.cacheChatRoom(chatRoom);
        return chatRoom;
    }
}