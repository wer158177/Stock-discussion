package com.hangha.stockservice.infrastructure.websoket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.stockservice.infrastructure.dto.TickerMessage;
import com.hangha.stockservice.infrastructure.service.UpbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UpbitWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, Map<String, WebSocketSession>> marketSubscriptions = new ConcurrentHashMap<>();
    private final UpbitTickerWebSocketService upbitTickerWebSocketService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UpbitService upbitService;
    private final String type;

    public UpbitWebSocketHandler(UpbitTickerWebSocketService upbitTickerWebSocketService, UpbitService upbitService, String type) {
        this.upbitTickerWebSocketService = upbitTickerWebSocketService;
        this.upbitService = upbitService;
        this.type = type;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("새로운 WebSocket 연결: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode jsonMessage = objectMapper.readTree(message.getPayload());
            String market = jsonMessage.get("market").asText();

            marketSubscriptions.computeIfAbsent(market, k -> new ConcurrentHashMap<>())
                    .put(session.getId(), session);

            if (type.equals("minute")) {
                String initialData = upbitService.getMinuteCandles(market, "1", null, 60);
                session.sendMessage(new TextMessage(initialData));
            } else if (type.equals("ticker")) {
                upbitTickerWebSocketService.subscribeToTickerWS(market, this);
            }

            log.info("마켓 정보 전송 완료 - 마켓: {}, 세션: {}", market, session.getId());
        } catch (IOException e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료: {}", session.getId());
        marketSubscriptions.values().forEach(sessions -> sessions.remove(session.getId()));
        if (marketSubscriptions.isEmpty()) {
            upbitTickerWebSocketService.closeWebSocket();
        }
    }

    public void handleTickerData(String message) {
        try {
            TickerMessage tickerMessage = objectMapper.readValue(message, TickerMessage.class);
            log.info("파싱된 Ticker 데이터: {}", tickerMessage);

            String market = tickerMessage.getCode();
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(tickerMessage));

            marketSubscriptions.getOrDefault(market, new ConcurrentHashMap<>()).values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    } else {
                        marketSubscriptions.get(market).remove(session.getId());
                    }
                } catch (IOException e) {
                    log.error("데이터 전송 실패 - 세션: {}", session.getId());
                    marketSubscriptions.get(market).remove(session.getId());
                }
            });
        } catch (Exception e) {
            log.error("TickerMessage 변환 실패: {}", e.getMessage());
        }
    }

    public void handleMinuteData(String message) {
        processMarketData(message, "분당 데이터 수신");
    }

    private void processMarketData(String message, String logMessage) {
        try {
            JsonNode jsonMessage = objectMapper.readTree(message);
            String market = jsonMessage.get("code").asText();

            String newData = jsonMessage.toString();
            log.info("{} - 마켓: {}", logMessage, market);

            TextMessage textMessage = new TextMessage(newData);
            marketSubscriptions.getOrDefault(market, new ConcurrentHashMap<>()).values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    } else {
                        marketSubscriptions.get(market).remove(session.getId());
                    }
                } catch (IOException e) {
                    log.error("데이터 전송 실패 - 세션: {}", session.getId());
                    marketSubscriptions.get(market).remove(session.getId());
                }
            });
        } catch (IOException e) {
            log.error("데이터 처리 실패: {}", e.getMessage());
        }
    }
}