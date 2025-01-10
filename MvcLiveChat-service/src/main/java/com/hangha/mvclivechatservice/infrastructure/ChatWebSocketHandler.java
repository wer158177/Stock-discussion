package com.hangha.mvclivechatservice.infrastructure;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.mvclivechatservice.application.ChatApplication;
import com.hangha.mvclivechatservice.domain.service.ChatService;
import com.hangha.mvclivechatservice.infrastructure.client.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ChatApplication chatApplication;

    public ChatWebSocketHandler(ChatApplication chatApplication) {
        this.chatApplication = chatApplication;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        chatApplication.handleConnection(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        chatApplication.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        chatApplication.handleDisconnection(session);
    }
}
