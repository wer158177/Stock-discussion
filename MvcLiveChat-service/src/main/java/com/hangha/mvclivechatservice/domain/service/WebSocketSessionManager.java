package com.hangha.mvclivechatservice.domain.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketSessionManager {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    /**
     * 세션 추가
     */
    public void addSession(String roomName, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomName, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    /**
     * 세션 제거
     */
    public void removeSession(String roomName, WebSocketSession session) {
        CopyOnWriteArrayList<WebSocketSession> sessions = roomSessions.get(roomName);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomName);
            }
        }
    }

    /**
     * 특정 방의 모든 세션 반환
     */
    public CopyOnWriteArrayList<WebSocketSession> getSessions(String roomName) {
        return roomSessions.getOrDefault(roomName, new CopyOnWriteArrayList<>());
    }
}