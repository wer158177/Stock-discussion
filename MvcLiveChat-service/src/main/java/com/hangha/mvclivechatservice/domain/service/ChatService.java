package com.hangha.mvclivechatservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.mvclivechatservice.domain.entity.ChatMessage;
import com.hangha.mvclivechatservice.domain.entity.ChatRoom;
import com.hangha.mvclivechatservice.domain.repository.ChatMessageRepository;
import com.hangha.mvclivechatservice.domain.repository.ChatRoomRepository;
import com.hangha.mvclivechatservice.infrastructure.dto.ChatMessageResponseDto;
import com.hangha.mvclivechatservice.infrastructure.dto.IncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    public ChatService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository,
                       WebSocketSessionManager sessionManager, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    /**
     * 메시지 저장
     */
    public ChatMessageResponseDto saveMessage(ChatRoom chatRoom, UserResponseDto senderInfo, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("[ChatService] Content cannot be null or empty");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderName(senderInfo.getUsername())
                .senderProfileUrl(senderInfo.getImageUrl())
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);


        return new ChatMessageResponseDto(savedMessage);
    }

    /**
     * 메시지 브로드캐스트
     */
    public void broadcastMessage(ChatRoom chatRoom, ChatMessageResponseDto responseDto, WebSocketSession senderSession) {
        try {

            String broadcastMessage = objectMapper.writeValueAsString(responseDto);

            for (WebSocketSession session : sessionManager.getSessions(chatRoom.getName())) {
                if (session.isOpen() && !session.getId().equals(senderSession.getId())) {
                    session.sendMessage(new TextMessage(broadcastMessage));
                }
            }
        } catch (Exception e) {
            log.error("[ChatService] 메시지 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to client", e);
        }
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
            log.warn("[ChatService] roomName is null for sessionId: {}", session.getId());
            return; // 종료
        }

        sessionManager.removeSession(roomName, session);
        log.info("[ChatService] 세션 제거됨 - sessionId: {}, roomName: {}", session.getId(), roomName);
    }


    public ChatRoom getChatRoomByName(String roomName) {
        ChatRoom chatRoom = chatRoomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Chat room with name " + roomName + " not found."));
        log.info("[ChatService] 채팅방 조회 완료: {}", roomName);
        return chatRoom;
    }
}