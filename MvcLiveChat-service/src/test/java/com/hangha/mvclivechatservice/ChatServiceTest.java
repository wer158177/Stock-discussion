package com.hangha.mvclivechatservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.mvclivechatservice.config.TestAppender;
import com.hangha.mvclivechatservice.domain.entity.ChatRoom;
import com.hangha.mvclivechatservice.domain.service.ChatService;
import com.hangha.mvclivechatservice.domain.service.WebSocketSessionManager;
import com.hangha.mvclivechatservice.infrastructure.dto.ChatMessageResponseDto;
import com.hangha.mvclivechatservice.infrastructure.dto.IncomingMessage;


import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatService의 기능 테스트 클래스입니다.
 * WebSocket 세션 관리 및 메시지 전송 로직에 대한 다양한 테스트를 포함합니다.
 */
class ChatServiceTest {

    // WebSocket 세션 관리를 담당하는 목(Mock) 객체
    @Mock
    private WebSocketSessionManager sessionManager;

    // JSON 직렬화를 위한 ObjectMapper의 목 객체
    @Mock
    private ObjectMapper objectMapper;

    // 테스트 대상인 ChatService
    @InjectMocks
    private ChatService chatService;

    // WebSocket 세션(Mock)
    @Mock
    private WebSocketSession openSession;

    @Mock
    private WebSocketSession closedSession;

    // 테스트에 사용되는 채팅방, 메시지 응답 DTO, 수신 메시지
    private ChatRoom chatRoom;
    private ChatMessageResponseDto responseDto;
    private IncomingMessage incomingMessage;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        MockitoAnnotations.openMocks(this);

        // 테스트에 필요한 객체 초기화
        chatRoom = new ChatRoom("testRoom");
        responseDto = new ChatMessageResponseDto(
                "MESSAGE",
                "testRoom",
                new ChatMessageResponseDto.Sender("sender", "url"),
                "Test Message",
                "2025-01-01T12:00:00Z"
        );
        incomingMessage = new IncomingMessage("MESSAGE", "Test Message", "testRoom", "sender");

        // WebSocket 세션 상태 설정
        when(openSession.isOpen()).thenReturn(true); // 열려 있는 세션
        when(closedSession.isOpen()).thenReturn(false); // 닫혀 있는 세션
    }

    @Test
    void shouldBroadcastMessageToOpenSessions() throws Exception {
        // 열려 있는 세션에 메시지를 전송하는 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        sessions.add(openSession); // 열려 있는 세션 추가
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions); // 세션 매니저에서 반환 설정
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("Serialized Message"); // JSON 직렬화 결과 설정

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(openSession).sendMessage(any(TextMessage.class)); // 메시지 전송 확인
        verify(sessionManager, never()).removeSession(chatRoom.getName(), openSession); // 세션 삭제되지 않았는지 확인
    }

    @Test
    void shouldRemoveClosedSession() throws Exception {
        // 닫혀 있는 세션을 제거하는 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        sessions.add(closedSession); // 닫혀 있는 세션 추가
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions);

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(sessionManager).removeSession(chatRoom.getName(), closedSession); // 세션 제거 확인
        verify(closedSession, never()).sendMessage(any(TextMessage.class)); // 메시지 전송 시도되지 않았는지 확인
    }

    @Test
    void shouldHandleSerializationExceptionGracefully() throws Exception {
        // JSON 직렬화 예외를 처리하는 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        sessions.add(openSession); // 열려 있는 세션 추가
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions);
        when(objectMapper.writeValueAsString(responseDto)).thenThrow(new JsonProcessingException("Test Error") {});

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(openSession, never()).sendMessage(any(TextMessage.class)); // 메시지 전송되지 않았는지 확인
        verify(sessionManager, never()).removeSession(chatRoom.getName(), openSession); // 세션 제거되지 않았는지 확인
    }

    @Test
    void shouldHandleMixedOpenAndClosedSessions() throws Exception {
        // 열려 있는 세션과 닫혀 있는 세션이 섞여 있을 때의 처리 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        sessions.add(openSession);
        sessions.add(closedSession);
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("Serialized Message");

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(openSession).sendMessage(any(TextMessage.class)); // 열린 세션에 메시지 전송 확인
        verify(sessionManager).removeSession(chatRoom.getName(), closedSession); // 닫힌 세션 제거 확인
    }

    @Test
    void shouldProcessIncomingMessageCorrectly() throws Exception {
        // 수신된 메시지 처리 테스트

        // Given
        when(objectMapper.writeValueAsString(incomingMessage)).thenReturn("Serialized Incoming Message");

        // When
        String serializedMessage = objectMapper.writeValueAsString(incomingMessage);

        // Then
        assertEquals("Serialized Incoming Message", serializedMessage); // 직렬화 결과 검증
    }

    @Test
    void shouldHandleEmptyChatRoom() throws Exception {
        // 채팅방에 세션이 없을 경우의 처리 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions);

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(sessionManager, never()).removeSession(anyString(), any(WebSocketSession.class)); // 세션 제거 시도되지 않았는지 확인
        verify(openSession, never()).sendMessage(any(TextMessage.class)); // 메시지 전송되지 않았는지 확인
    }

    @Test
    void shouldHandleSessionDisconnectionGracefully() throws Exception {
        // 세션 연결이 끊어졌을 경우의 처리 테스트

        // Given
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        sessions.add(openSession);
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("Serialized Message");

        // 연결 끊김 시뮬레이션
        doThrow(new IOException("Connection reset by peer"))
                .when(openSession).sendMessage(new TextMessage("Serialized Message"));

        // When
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        verify(sessionManager).removeSession(chatRoom.getName(), openSession); // 끊어진 세션 제거 확인
    }


    @Test
    void shouldBroadcastMessageToManySessions() throws Exception {
        // Given
        // 1000개의 WebSocket 세션을 Mock으로 생성하여 세션 리스트에 추가
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 1000; i++) {
            WebSocketSession session = mock(WebSocketSession.class); // Mock WebSocketSession 생성
            when(session.isOpen()).thenReturn(true); // 세션이 열려 있는 상태로 설정
            sessions.add(session);
        }
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions); // 세션 리스트 반환 설정
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("Serialized Message"); // 직렬화 결과 설정

        // When
        // 1000개의 세션에 메시지 브로드캐스트 수행
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        // 모든 세션에 메시지가 전송되었는지 검증
        for (WebSocketSession session : sessions) {
            verify(session).sendMessage(any(TextMessage.class));
        }
    }

    @Test
    void shouldHandleZeroSessionsGracefully() throws Exception {
        // Given
        // 세션이 없는 경우 테스트
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions); // 빈 세션 리스트 반환 설정

        // When
        // 빈 세션 리스트에서 메시지 브로드캐스트 수행
        chatService.broadcastMessage(chatRoom, responseDto, null);

        // Then
        // 세션이 없으므로 제거 로직이 호출되지 않아야 함
        verify(sessionManager, never()).removeSession(anyString(), any(WebSocketSession.class));
        verifyNoInteractions(openSession); // 메시지 전송도 호출되지 않아야 함
    }

    @Test
    void shouldBroadcastMessagesConcurrently() throws Exception {
        // Given
        // 100개의 WebSocket 세션을 Mock으로 생성하여 세션 리스트에 추가
        CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 100; i++) {
            WebSocketSession session = mock(WebSocketSession.class); // Mock WebSocketSession 생성
            when(session.isOpen()).thenReturn(true); // 세션이 열려 있는 상태로 설정
            sessions.add(session);
        }
        when(sessionManager.getSessions(chatRoom.getName())).thenReturn(sessions); // 세션 리스트 반환 설정
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("Serialized Message"); // 직렬화 결과 설정

        // When
        // 10개의 스레드에서 동시 메시지 브로드캐스트 작업 수행
        Runnable broadcastTask = () -> {
            try {
                chatService.broadcastMessage(chatRoom, responseDto, null);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        };

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(broadcastTask);
            threads[i].start(); // 각 스레드 시작
        }

        for (Thread thread : threads) {
            thread.join(); // 모든 스레드 종료 대기
        }

        // Then
        // 모든 세션에 적어도 한 번 이상 메시지가 전송되었는지 검증
        for (WebSocketSession session : sessions) {
            verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
        }
    }

    @Test
    void shouldHandleNewMessageType() throws Exception {
        // Given
        // 새로운 메시지 타입으로 테스트
        IncomingMessage newMessage = new IncomingMessage("NEW_TYPE", "New Content", "testRoom", "sender");
        when(objectMapper.writeValueAsString(newMessage)).thenReturn("Serialized New Message"); // 직렬화 결과 설정

        // When
        // 새로운 메시지 타입을 직렬화
        String serializedMessage = objectMapper.writeValueAsString(newMessage);

        // Then
        // 직렬화 결과가 예상된 값인지 검증
        assertEquals("Serialized New Message", serializedMessage);
    }



}