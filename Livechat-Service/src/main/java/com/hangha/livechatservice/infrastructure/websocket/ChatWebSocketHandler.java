package com.hangha.livechatservice.infrastructure.websocket;


import com.hangha.livechatservice.handlers.ConnectionHandler;
import com.hangha.livechatservice.handlers.DisconnectionHandler;
import com.hangha.livechatservice.handlers.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ConnectionHandler connectionHandler;
    private final MessageHandler messageHandler;
    private final DisconnectionHandler disconnectionHandler;

    public ChatWebSocketHandler( ConnectionHandler connectionHandler, MessageHandler messageHandler, DisconnectionHandler disconnectionHandler) {
        this.connectionHandler = connectionHandler;
        this.messageHandler = messageHandler;
        this.disconnectionHandler = disconnectionHandler;
    }

//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        return chatApplication.handleConnection(session)
//                .then(session.receive()
//                        .map(msg -> msg.getPayloadAsText())
//                        .flatMap(msg -> chatApplication.handleMessage(session, msg))
//                        .then())
//                .doFinally(signalType -> chatApplication.handleDisconnection(session).subscribe());
//    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return connectionHandler.handleConnection(session) // 연결 처리
                .then(session.receive()
                        .map(msg -> msg.getPayloadAsText())
                        .flatMap(msg -> messageHandler.handleMessage(session, msg)) // 메시지 처리
                        .then())
                .doFinally(signalType -> disconnectionHandler.handleDisconnection(session).subscribe()); // 연결 종료 처리

    }
}
