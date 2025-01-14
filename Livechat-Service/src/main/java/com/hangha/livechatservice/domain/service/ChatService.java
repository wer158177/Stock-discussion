package com.hangha.livechatservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.livechatservice.domain.entity.ChatMessage;
import com.hangha.livechatservice.domain.repository.ChatMessageRepository;
import com.hangha.livechatservice.infrastructure.dto.ChatMessageResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ChatService {

    private final WebSocketSessionManager sessionManager;
    private final ChatMessageRepository chatMessageRepository;

    private final Sinks.Many<ChatMessage> messageSink = Sinks.many().unicast().onBackpressureBuffer();
    private final Queue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private static final int BATCH_SIZE = 1000;
    private static final Duration FLUSH_INTERVAL = Duration.ofSeconds(1);

    public ChatService(WebSocketSessionManager sessionManager, ChatMessageRepository chatMessageRepository) {
        this.sessionManager = sessionManager;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Mono<Void> addUserToRoom(WebSocketSession session, UserResponseDto userInfo, String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Room name cannot be null or empty"));
        }

        session.getAttributes().put("userInfo", userInfo);
        session.getAttributes().put("roomName", roomName);

        sessionManager.addSession(roomName, session);
        return Mono.empty();
    }

    public Mono<Void> removeUserFromRoom(WebSocketSession session) {
        String roomName = (String) session.getAttributes().get("roomName");
        if (roomName != null) {
            sessionManager.removeSession(roomName, session);
        }
        return Mono.empty();
    }

    public Mono<Void> broadcastMessage(WebSocketSession senderSession, ChatMessageResponseDto responseDto) {
        String roomName = responseDto.getRoomName();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String broadcastMessage = objectMapper.writeValueAsString(responseDto);

            return sessionManager.getSessions(roomName)
                    .filter(session -> !session.getId().equals(senderSession.getId()))
                    .flatMap(session -> session.send(Mono.just(session.textMessage(broadcastMessage))))
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Failed to serialize message", e));
        }
    }

    public Flux<ChatMessage> getMessagesForRoom(String roomName) {
        return chatMessageRepository.findByChatRoomName(roomName);
    }
}
