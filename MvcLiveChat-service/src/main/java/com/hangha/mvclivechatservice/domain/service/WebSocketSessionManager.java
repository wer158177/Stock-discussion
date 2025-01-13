package com.hangha.mvclivechatservice.domain.service;

import org.springframework.scheduling.annotation.Scheduled;
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


    /**
     * 닫힌 세션 정리 작업 (1분마다 실행)
     */
    @Scheduled(fixedRate = 60000) // 1분 간격으로 실행
    public void cleanUpClosedSessions() {
        roomSessions.forEach((roomName, sessions) -> {
            sessions.removeIf(session -> {
                if (!session.isOpen()) {
                    System.out.printf("[WebSocketSessionManager] 닫힌 세션 정리 (방 이름: %s, 세션 ID: %s)%n", roomName, session.getId());
                    return true; // 닫힌 세션 제거
                }
                return false;
            });

            // 세션 리스트가 비었으면 방 제거
            if (sessions.isEmpty()) {
                roomSessions.remove(roomName);
                System.out.printf("[WebSocketSessionManager] 빈 방 제거 (방 이름: %s)%n", roomName);
            }
        });
    }
}