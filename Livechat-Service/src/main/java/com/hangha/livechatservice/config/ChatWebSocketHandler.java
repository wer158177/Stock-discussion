package com.hangha.livechatservice.config;

import com.hangha.livechatservice.application.ChatMessageApplicationService;
import com.hangha.livechatservice.domain.entity.ChatMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatMessageApplicationService chatMessageApplicationService;
    private final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatMessageApplicationService chatMessageApplicationService) {
        this.chatMessageApplicationService = chatMessageApplicationService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        String userId = getUserId(session);

        // 메시지 저장
        ChatMessage savedMessage = chatMessageApplicationService.saveMessage(
                Long.parseLong(roomId),
                userId,
                message.getPayload()
        );

        // 브로드캐스트
        broadcastMessage(roomId, userId, message.getPayload());
    }

    private void broadcastMessage(String roomId, String userId, String message) {
        rooms.getOrDefault(roomId, Map.of()).values().stream()
                .filter(WebSocketSession::isOpen)
                .forEach(client -> {
                    try {
                        client.sendMessage(new TextMessage(userId + ": " + message));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to send message", e);
                    }
                });
    }
}