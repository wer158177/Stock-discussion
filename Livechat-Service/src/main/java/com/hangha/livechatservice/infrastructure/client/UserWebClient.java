package com.hangha.livechatservice.infrastructure.client;

import com.hangha.common.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserWebClient {

    private final WebClient webClient;

    public UserWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UserResponseDto> getUserInfo(Long userId) {
        return webClient.get()
                .uri("/api/user/{userId}", userId) // 기본 URL은 WebClientConfig에서 설정
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .doOnNext(userInfo -> log.info("[UserWebClient] 사용자 정보 조회 성공: userId={}, username={}", userId, userInfo.getUsername()))
                .onErrorResume(e -> {
                    return Mono.empty(); // 기본값 반환
                });
    }
}
