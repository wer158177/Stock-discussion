package com.hangha.livechatservice.infrastructure.websocket;

import com.hangha.livechatservice.application.ChatApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatApplication chatApplication;

    public ChatWebSocketHandler(ChatApplication chatApplication) {
        this.chatApplication = chatApplication;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return chatApplication.handleConnection(session)
                .then(session.receive()
                        .map(msg -> msg.getPayloadAsText())
                        .flatMap(msg -> chatApplication.handleMessage(session, msg))
                        .then())
                .doFinally(signalType -> chatApplication.handleDisconnection(session).subscribe());
    }
}
