package com.hangha.livechatservice.domain.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketSessionManager {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    public void addSession(String roomName, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomName, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    public void removeSession(String roomName, WebSocketSession session) {
        CopyOnWriteArrayList<WebSocketSession> sessions = roomSessions.get(roomName);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomName);
            }
        }
    }

    public Flux<WebSocketSession> getSessions(String roomName) {
        return Flux.fromIterable(roomSessions.getOrDefault(roomName, new CopyOnWriteArrayList<>()));
    }
}
