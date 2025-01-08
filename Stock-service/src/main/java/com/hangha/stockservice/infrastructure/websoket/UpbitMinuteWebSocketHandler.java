package com.hangha.stockservice.infrastructure.websoket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.stockservice.infrastructure.service.UpbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UpbitMinuteWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, Map<String, WebSocketSession>> marketSubscriptions = new ConcurrentHashMap<>();
    private final UpbitService upbitService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, LocalDateTime> lastUpdateTimes = new ConcurrentHashMap<>();

    public UpbitMinuteWebSocketHandler(UpbitService upbitService) {
        this.upbitService = upbitService;
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

            // 초기 60개 분봉 데이터 요청
            String initialData = upbitService.getMinuteCandles(market, "1", null, 60);
            session.sendMessage(new TextMessage(initialData));

            lastUpdateTimes.put(market, LocalDateTime.now());
            log.info("초기 데이터 전송 완료 - 마켓: {}, 세션: {}", market, session.getId());
        } catch (IOException e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void updateMinuteData() {
        marketSubscriptions.forEach((market, sessions) -> {
            if (!sessions.isEmpty()) {
                try {
                    // 1분 경과, 60개 분봉 데이터 요청
                    String newData = upbitService.getMinuteCandles(market, "1", null, 60);
                    lastUpdateTimes.put(market, LocalDateTime.now());
                    log.info("1분 경과, 60개 분봉 데이터 전송 - 마켓: {}", market);

                    TextMessage textMessage = new TextMessage(newData);
                    sessions.values().forEach(session -> {
                        try {
                            if (session.isOpen()) {
                                session.sendMessage(textMessage);
                            } else {
                                sessions.remove(session.getId());
                            }
                        } catch (IOException e) {
                            log.error("데이터 전송 실패 - 세션: {}", session.getId());
                            sessions.remove(session.getId());
                        }
                    });
                } catch (Exception e) {
                    log.error("마켓 데이터 조회 실패 - 마켓: {}", market);
                }
            }
        });
    }

    public void handleMinuteData(String message) {
        try {
            JsonNode jsonMessage = objectMapper.readTree(message);
            String market = jsonMessage.get("code").asText();

            String newData = jsonMessage.toString();
            log.info("분당 데이터 수신 - 마켓: {}", market);

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
            log.error("분당 데이터 처리 실패: {}", e.getMessage());
        }
    }
}