package com.hangha.livechatservice.handlers;


import com.hangha.livechatservice.domain.service.WebSocketSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
public class DisconnectionHandler {
    private final WebSocketSessionManager webSocketSessionManager;

    public DisconnectionHandler(WebSocketSessionManager webSocketSessionManager) {

        this.webSocketSessionManager = webSocketSessionManager;
    }

    public Mono<Void> handleDisconnection(WebSocketSession session) {
        return webSocketSessionManager.removeUserFromRoom(session);
    }

}
