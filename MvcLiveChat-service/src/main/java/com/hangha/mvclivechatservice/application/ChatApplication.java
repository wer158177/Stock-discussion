package com.hangha.mvclivechatservice.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.mvclivechatservice.domain.entity.ChatMessage;
import com.hangha.mvclivechatservice.domain.entity.ChatRoom;
import com.hangha.mvclivechatservice.domain.repository.ChatRoomRepository;
import com.hangha.mvclivechatservice.domain.service.ChatService;
import com.hangha.mvclivechatservice.infrastructure.client.UserFeignClient;
import com.hangha.mvclivechatservice.infrastructure.cache.UserInfoService;
import com.hangha.mvclivechatservice.infrastructure.dto.ChatMessageResponseDto;
import com.hangha.mvclivechatservice.infrastructure.dto.IncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
public class ChatApplication {

    private final ChatService chatService;
    private final UserInfoService userInfoService;
    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;


    public ChatApplication(ChatService chatService, UserFeignClient userFeignClient, UserInfoService userInfoService, ObjectMapper objectMapper, ChatRoomRepository chatRoomRepository) {
        this.chatService = chatService;
        this.userInfoService = userInfoService;
        this.objectMapper = objectMapper;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void handleConnection(WebSocketSession session) {
        try {
            String userIdHeader = session.getHandshakeHeaders().getFirst("X-Claim-userId");
            String roomName = session.getHandshakeHeaders().getFirst("X-Room-Name");

            if (userIdHeader == null || roomName == null) {
                throw new IllegalArgumentException("Missing userId or roomName");
            }

            Long userId = Long.parseLong(userIdHeader);
            UserResponseDto userInfo = userInfoService.getUserInfo(userId);

            // 추가: roomName 초기화 및 검증
            if (roomName.isEmpty()) {
                throw new IllegalArgumentException("RoomName cannot be empty");
            }

            ChatRoom chatRoom = chatService.addUserToRoom(session, userInfo, roomName);

        } catch (Exception e) {
            log.error("[ChatApplication] Connection 실패: {}", e.getMessage(), e);
            sendErrorMessage(session, "Failed to connect: " + e.getMessage());
        }
    }

//    public void handleMessage(WebSocketSession session, TextMessage message) {
//        try {
//            String payload = message.getPayload();
//            IncomingMessage incomingMessage = objectMapper.readValue(payload, IncomingMessage.class);
//
//            if ("LEAVE_ROOM".equals(incomingMessage.getType())) {
//                String roomName = incomingMessage.getRoomName();
//                chatService.removeUserFromRoom(session);
//                return;
//            }
//
//            // 기존 메시지 처리 로직
//            String roomName = incomingMessage.getRoomName();
//            String content = incomingMessage.getContent();
//
//            Long userId = (Long) session.getAttributes().get("userId");
//            String username = (String) session.getAttributes().get("username");
//            String profileUrl = (String) session.getAttributes().get("profileUrl");
//
//            UserResponseDto senderInfo = new UserResponseDto(userId, username, profileUrl);
//            ChatMessageResponseDto responseDto = chatService.saveMessage(roomName, senderInfo, content);
//
//            ChatRoom chatRoom = chatService.getChatRoomByName(roomName);
//            chatService.broadcastMessage(chatRoom, responseDto, session);
//        } catch (Exception e) {
//            log.error("[ChatApplication] 메시지 처리 실패: {}", e.getMessage(), e);
//            sendErrorMessage(session, "Failed to process message: " + e.getMessage());
//        }
//    }


    public void handleMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            IncomingMessage incomingMessage = objectMapper.readValue(payload, IncomingMessage.class);

            if ("LEAVE_ROOM".equals(incomingMessage.getType())) {
                chatService.removeUserFromRoom(session);
                return;
            }

            // 메시지 생성 및 큐에 추가
            String roomName = incomingMessage.getRoomName();
            String content = incomingMessage.getContent();
            Long userId = (Long) session.getAttributes().get("userId");
            String username = (String) session.getAttributes().get("username");
            String profileUrl = (String) session.getAttributes().get("profileUrl");

            // 채팅방 조회 (캐시 또는 DB에서)
            ChatRoom chatRoom = chatService.getChatRoomByName(roomName);


            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoomName(chatRoom.getName())
                    .senderName(username)
                    .senderProfileUrl(profileUrl)
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .build();

            chatService.addMessageToQueue(chatMessage);

            // 브로드캐스트 로직 유지
            ChatMessageResponseDto responseDto = new ChatMessageResponseDto(chatMessage);
            chatService.broadcastMessage(chatRoom, responseDto, session);

        } catch (Exception e) {
            log.error("[ChatApplication] 메시지 처리 실패: {}", e.getMessage(), e);
        }
    }










    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            session.sendMessage(new TextMessage("{\"error\":\"" + errorMessage + "\"}"));
            log.info("[ChatApplication] 에러 메시지 전송: {}", errorMessage);
        } catch (Exception e) {
            log.error("[ChatApplication] Failed to send error message: {}", e.getMessage(), e);
        }
    }

    public void handleDisconnection(WebSocketSession session) {
        try {
            String roomName = (String) session.getAttributes().get("roomName");

            if (roomName == null) {
                log.warn("[ChatApplication] roomName is null for sessionId: {}", session.getId());
            }
            chatService.removeUserFromRoom(session);

        } catch (Exception e) {
            log.error("[ChatApplication] 연결 종료 처리 실패: {}", e.getMessage(), e);
        }
    }
}