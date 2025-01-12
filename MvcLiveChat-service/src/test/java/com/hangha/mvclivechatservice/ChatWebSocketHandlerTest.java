package com.hangha.mvclivechatservice;

import com.hangha.mvclivechatservice.application.ChatApplication;
import com.hangha.mvclivechatservice.infrastructure.websocket.ChatWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class ChatWebSocketHandlerTest {

    @Mock
    private ChatApplication chatApplication;

    @InjectMocks
    private ChatWebSocketHandler chatWebSocketHandler;

    @Mock
    private WebSocketSession session;

    @Test
    void afterConnectionEstablished_ShouldLogConnection() throws Exception {
        // Given
        doNothing().when(chatApplication).handleConnection(session);

        // When
        chatWebSocketHandler.afterConnectionEstablished(session);

        // Then
        verify(chatApplication, times(1)).handleConnection(session);
        verifyNoMoreInteractions(chatApplication);
    }

    @Test
    void handleTextMessage_ShouldLogMessage() throws Exception {
        // Given
        TextMessage message = new TextMessage("test message");
        doNothing().when(chatApplication).handleMessage(session, message);

        // When
        chatWebSocketHandler.handleTextMessage(session, message);

        // Then
        verify(chatApplication, times(1)).handleMessage(session, message);
        verifyNoMoreInteractions(chatApplication);
    }

    @Test
    void afterConnectionClosed_ShouldLogDisconnection() throws Exception {
        // Given
        CloseStatus status = CloseStatus.NORMAL;
        doNothing().when(chatApplication).handleDisconnection(session);

        // When
        chatWebSocketHandler.afterConnectionClosed(session, status);

        // Then
        verify(chatApplication, times(1)).handleDisconnection(session);
        verifyNoMoreInteractions(chatApplication);
    }

    @Test
    void afterConnectionEstablished_ShouldHandleException() throws Exception {
        // Given
        doThrow(new RuntimeException("Connection error")).when(chatApplication).handleConnection(session);

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            chatWebSocketHandler.afterConnectionEstablished(session);
        });

        // Then
        assertEquals("Connection error", exception.getMessage());
        verify(chatApplication, times(1)).handleConnection(session);
    }

    @Test
    void handleTextMessage_ShouldHandleException() throws Exception {
        // Given
        TextMessage message = new TextMessage("test message");
        doThrow(new RuntimeException("Message error")).when(chatApplication).handleMessage(session, message);

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            chatWebSocketHandler.handleTextMessage(session, message);
        });

        // Then
        assertEquals("Message error", exception.getMessage());
        verify(chatApplication, times(1)).handleMessage(session, message);
    }

    @Test
    void afterConnectionClosed_ShouldHandleException() throws Exception {
        // Given
        CloseStatus status = CloseStatus.NORMAL;
        doThrow(new RuntimeException("Disconnection error")).when(chatApplication).handleDisconnection(session);

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            chatWebSocketHandler.afterConnectionClosed(session, status);
        });

        // Then
        assertEquals("Disconnection error", exception.getMessage());
        verify(chatApplication, times(1)).handleDisconnection(session);
    }
}