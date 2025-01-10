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
        System.out.println("[WebSocketSessionManager] 세션 추가됨: " + session.getId() + " 방: " + roomName);
        System.out.println("[WebSocketSessionManager] 현재 방 '" + roomName + "'의 세션 수: " + roomSessions.get(roomName).size());
    }

    /**
     * 세션 제거
     */
    public void removeSession(String roomName, WebSocketSession session) {
        CopyOnWriteArrayList<WebSocketSession> sessions = roomSessions.get(roomName);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("[WebSocketSessionManager] 세션 제거됨: " + session.getId() + " 방: " + roomName);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomName);
                System.out.println("[WebSocketSessionManager] 방 '" + roomName + "'의 모든 세션이 제거되었습니다.");
            }
        }
    }

    /**
     * 특정 방의 모든 세션 반환
     */
    public CopyOnWriteArrayList<WebSocketSession> getSessions(String roomName) {
        System.out.println("[WebSocketSessionManager] 방 '" + roomName + "'의 세션 목록 요청됨.");
        return roomSessions.getOrDefault(roomName, new CopyOnWriteArrayList<>());
    }
}