package com.hangha.activityservice.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class UserWebClient {
    private final WebClient webClient;

    public UserWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public Flux<Long> getFollowers(Long userId, Long cursor, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/following/{userId}/followers")
                        .queryParam("cursor", cursor)
                        .queryParam("size", size)
                        .build(userId))  // Uri 템플릿에 변수 userId를 삽입
                .retrieve()
                .bodyToFlux(Long.class);  // Flux<Long> 반환
    }
}

