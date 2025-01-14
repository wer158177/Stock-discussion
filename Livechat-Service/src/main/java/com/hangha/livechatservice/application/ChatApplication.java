package com.hangha.livechatservice.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.livechatservice.domain.service.BatchProcessor;
import com.hangha.livechatservice.domain.entity.ChatMessage;
import com.hangha.livechatservice.domain.service.ChatService;
import com.hangha.livechatservice.infrastructure.cache.ChatRoomCacheManager;
import com.hangha.livechatservice.infrastructure.cache.UserInfoService;
import com.hangha.livechatservice.infrastructure.dto.ChatMessageResponseDto;
import com.hangha.livechatservice.infrastructure.dto.IncomingMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ChatApplication {

    private final ChatService chatService;
    private final UserInfoService userInfoService;
    private final ChatRoomCacheManager chatRoomCacheManager;
    private final BatchProcessor batchProcessor;

    public ChatApplication(ChatService chatService, UserInfoService userInfoService, ChatRoomCacheManager chatRoomCacheManager, BatchProcessor batchProcessor) {
        this.chatService = chatService;
        this.userInfoService = userInfoService;
        this.chatRoomCacheManager = chatRoomCacheManager;
        this.batchProcessor = batchProcessor;
    }

    public Mono<Void> handleConnection(WebSocketSession session) {
        String userIdHeader = session.getHandshakeInfo().getHeaders().getFirst("X-Claim-userId");
        String roomName = session.getHandshakeInfo().getHeaders().getFirst("X-Room-Name");

        if (userIdHeader == null || roomName == null) {
            return Mono.error(new IllegalArgumentException("Missing userId or roomName in headers"));
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            return Mono.error(new IllegalArgumentException("Invalid userId format"));
        }

        return userInfoService.getUserInfo(userId)
                .flatMap(userInfo -> chatService.addUserToRoom(session, userInfo, roomName));
    }

    public Mono<Void> handleMessage(WebSocketSession session, String messagePayload) {
        return Mono.defer(() -> {
            IncomingMessage incomingMessage;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                incomingMessage = objectMapper.readValue(messagePayload, IncomingMessage.class);
            } catch (Exception e) {
                return sendErrorMessage(session, "Invalid message format");
            }

            String roomName = (String) session.getAttributes().get("roomName");
            if (roomName == null) {
                return sendErrorMessage(session, "Room name is missing");
            }

            return chatRoomCacheManager.getChatRoom(roomName)
                    .flatMap(chatRoom -> {
                        UserResponseDto userInfo = (UserResponseDto) session.getAttributes().get("userInfo");
                        if (userInfo == null) {
                            return sendErrorMessage(session, "User info is missing");
                        }

                        ChatMessage chatMessage = ChatMessage.builder()
                                .chatRoomName(roomName)
                                .senderName(userInfo.getUsername())
                                .senderProfileUrl(userInfo.getImageUrl())
                                .content(incomingMessage.getContent())
                                .createdAt(LocalDateTime.now())
                                .build();

                        batchProcessor.addMessageToQueue(chatMessage);

                        ChatMessageResponseDto responseDto = new ChatMessageResponseDto(
                                "MESSAGE", chatMessage.getChatRoomName(),
                                new ChatMessageResponseDto.Sender(chatMessage.getSenderName(), chatMessage.getSenderProfileUrl()),
                                chatMessage.getContent(),
                                String.valueOf(chatMessage.getCreatedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli())
                        );

                        return chatService.broadcastMessage(session, responseDto);
                    });
        });
    }

    public Mono<Void> sendErrorMessage(WebSocketSession session, String errorMessage) {
        String errorJson = "{\"error\":\"" + errorMessage + "\"}";
        return session.send(Mono.just(session.textMessage(errorJson)));
    }

    public Mono<Void> handleDisconnection(WebSocketSession session) {
        String roomName = (String) session.getAttributes().get("roomName");
        return chatService.removeUserFromRoom(session);
    }
}
