package com.hangha.stockservice.infrastructure.websoket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UpbitTickerWebSocketService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebSocket upbitWebSocket;
    private UpbitWebSocketHandler upbitWebSocketHandler;

    public UpbitTickerWebSocketService() {
        // WebSocket은 필요 시 연결되며 초기화 시에는 연결하지 않습니다.
    }

    private void connectToUpbitWebSocket(String market) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .pingInterval(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("wss://api.upbit.com/websocket/v1")
                    .build();

            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    log.info("Upbit WebSocket 연결 성공");
                    upbitWebSocket = webSocket;

                    // 연결 완료 후 구독 요청 전송
                    sendSubscriptionMessage(market);
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    handleReceivedMessage(text);
                    log.info("스트링타입으로옴");
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    String text = bytes.string(StandardCharsets.UTF_8);
                    handleReceivedMessage(text);
                    log.info("바이트타입으로옴");
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    log.error("Upbit WebSocket 연결 실패: {}", t.getMessage(), t);
                    reconnectToUpbitWebSocket(market);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    log.warn("Upbit WebSocket 연결 종료: {} - 이유: {}", code, reason);
                    upbitWebSocket = null;
                }
            };

            upbitWebSocket = client.newWebSocket(request, listener);
        } catch (Exception e) {
            log.error("Upbit WebSocket 초기화 실패: {}", e.getMessage(), e);
        }
    }

    private void reconnectToUpbitWebSocket(String market) {
        log.info("Upbit WebSocket 재연결 시도");
        try {
            Thread.sleep(5000);
            connectToUpbitWebSocket(market);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("재연결 대기 중 인터럽트 발생: {}", e.getMessage(), e);
        }
    }

    private void sendSubscriptionMessage(String market) {
        if (upbitWebSocket != null) {
            String subscribeMsg = createSubscriptionMessage(market);
            upbitWebSocket.send(subscribeMsg);
            log.info("Ticker WebSocket 구독 요청 전송: {}", market);
        } else {
            log.error("Upbit WebSocket이 연결되지 않았습니다.");
        }
    }

    private String createSubscriptionMessage(String market) {
        return String.format(
                "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"%s\"]}]",
                market
        );
    }

    private void handleReceivedMessage(String text) {
        log.info("Upbit WebSocket 데이터 수신: {}", text);

        try {
            JsonNode jsonNode = objectMapper.readTree(text);
            log.info("수신된 JSON 데이터: {}", jsonNode);

            // 핸들러로 데이터 전달
            if (upbitWebSocketHandler != null) {
                upbitWebSocketHandler.handleTickerData(text);
            }
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", e.getMessage(), e);
        }
    }

    public void subscribeToTickerWS(String market, UpbitWebSocketHandler handler) {
        // 핸들러 등록 및 WebSocket 연결 생성
        this.upbitWebSocketHandler = handler;
        connectToUpbitWebSocket(market);
    }

    public void closeWebSocket() {
        if (upbitWebSocket != null) {
            upbitWebSocket.close(1000, "클라이언트 연결 종료");
            upbitWebSocket = null;
            log.info("Upbit WebSocket 연결 종료 요청");
        }
    }
}