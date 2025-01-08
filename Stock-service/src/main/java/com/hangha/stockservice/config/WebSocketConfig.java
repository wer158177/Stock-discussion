package com.hangha.stockservice.config;

import com.hangha.stockservice.infrastructure.websoket.UpbitMinuteWebSocketHandler;
import com.hangha.stockservice.infrastructure.websoket.UpbitTickerWebSocketService;
import com.hangha.stockservice.infrastructure.websoket.UpbitWebSocketHandler;
import com.hangha.stockservice.infrastructure.service.UpbitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UpbitService upbitService;
    private final UpbitTickerWebSocketService upbitTickerWebSocketService;

    public WebSocketConfig(UpbitService upbitService, UpbitTickerWebSocketService upbitTickerWebSocketService) {
        this.upbitService = upbitService;
        this.upbitTickerWebSocketService = upbitTickerWebSocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tickerWebSocketHandler(), "/ws/ticker")
                .setAllowedOrigins("*");
        registry.addHandler(minuteWebSocketHandler(), "/ws/minute")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler tickerWebSocketHandler() {
        return new UpbitWebSocketHandler(upbitTickerWebSocketService, upbitService, "ticker");
    }

    @Bean
    public WebSocketHandler minuteWebSocketHandler() {
        return new UpbitMinuteWebSocketHandler(upbitService);
    }
}