package com.hangha.livechatservice.handlers;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.livechatservice.domain.service.WebSocketSessionManager;
import com.hangha.livechatservice.infrastructure.cache.ChatRoomCacheManager;
import com.hangha.livechatservice.infrastructure.cache.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
public class ConnectionHandler {

    private final UserInfoService userInfoService;
    private final WebSocketSessionManager sessionManager;
    private final ChatRoomCacheManager chatRoomCacheManager;

    public ConnectionHandler(UserInfoService userInfoService, WebSocketSessionManager sessionManager, ChatRoomCacheManager chatRoomCacheManager) {
        this.userInfoService = userInfoService;
        this.sessionManager = sessionManager;
        this.chatRoomCacheManager = chatRoomCacheManager;
    }

    public Mono<Void> handleConnection(WebSocketSession session) {
        String userIdHeader = session.getHandshakeInfo().getHeaders().getFirst("X-Claim-userId");
        String roomName = session.getHandshakeInfo().getHeaders().getFirst("X-Room-Name");

        if (userIdHeader == null || roomName == null) {
            return Mono.error(new IllegalArgumentException("헤더에 userId 또는 roomName이 누락되었습니다."));
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            return Mono.error(new IllegalArgumentException("userId 형식이 올바르지 않습니다."));
        }

        // 방 검증 및 연결 처리
        return chatRoomCacheManager.getChatRoom(roomName)
                .switchIfEmpty(Mono.defer(() -> sendErrorMessage(session, "방이 존재하지 않습니다: " + roomName)
                        .then(Mono.error(new IllegalArgumentException("방이 존재하지 않습니다: " + roomName)))))
                .flatMap(chatRoom -> userInfoService.getUserInfo(userId))
                .flatMap(userInfo -> sessionManager.addUserToRoom(session, userInfo, roomName))
                .onErrorResume(error -> sendErrorMessage(session, error.getMessage()).then());

    }


    private Mono<Void> sendErrorMessage(WebSocketSession session, String errorMessage) {
        String errorJson = "{\"error\":\"" + errorMessage + "\"}";
        return session.send(Mono.just(session.textMessage(errorJson)))
                .then(Mono.empty()); // 에러 전송 후 종료
    }


}

