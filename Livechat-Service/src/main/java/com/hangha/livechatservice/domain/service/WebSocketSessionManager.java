package com.hangha.livechatservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.livechatservice.infrastructure.dto.ChatMessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class WebSocketSessionManager {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    public Mono<Void> addUserToRoom(WebSocketSession session, UserResponseDto userInfo, String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("방 이름은 비어있을 수 없습니다."));
        }

        try {
            // 세션에 사용자 정보와 방 이름 저장
            session.getAttributes().put("userInfo", userInfo);
            session.getAttributes().put("roomName", roomName);

            // 세션 매니저에 추가
            roomSessions.computeIfAbsent(roomName, k -> new CopyOnWriteArrayList<>()).add(session);

            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("사용자를 방에 추가하는 중 오류가 발생했습니다.", e));
        }
    }




    public Mono<Void> removeUserFromRoom(WebSocketSession session) {
        String roomName = (String) session.getAttributes().get("roomName");

        if (roomName == null) {
            log.warn("세션 '{}'에 방 이름이 설정되지 않았습니다.", session.getId());
            return Mono.empty();
        }

        CopyOnWriteArrayList<WebSocketSession> sessions = roomSessions.get(roomName);
        if (sessions == null) {
            log.warn("방 '{}'에 대한 세션 목록을 찾을 수 없습니다.", roomName);
            return Mono.empty();
        }

        synchronized (sessions) {
            if (sessions.remove(session)) {
                log.info("세션 '{}'이 방 '{}'에서 제거되었습니다.", session.getId(), roomName);

                // 방이 비어 있으면 제거
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomName);
                    log.info("방 '{}'이 비어 있어 제거되었습니다.", roomName);
                }
            } else {
                log.warn("세션 '{}'을 방 '{}'에서 제거할 수 없습니다. 세션이 존재하지 않을 수 있습니다.", session.getId(), roomName);
            }
        }

        // 세션 닫기 처리
        if (session.isOpen()) {
            session.close()
                    .doOnSuccess(aVoid -> log.info("세션 '{}'이 닫혔습니다.", session.getId()))
                    .doOnError(error -> log.warn("세션 '{}' 닫기 실패: {}", session.getId(), error.getMessage()))
                    .subscribe();
        }

        return Mono.empty();
    }







    public Flux<WebSocketSession> getSessions(String roomName) {
        return Flux.fromIterable(roomSessions.getOrDefault(roomName, new CopyOnWriteArrayList<>()));
    }
}
