package com.hangha.livechatservice.infrastructure.cache;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.livechatservice.infrastructure.client.UserWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserWebClient userWebClient;
    private final ReactiveRedisTemplate<String, UserResponseDto> redisTemplate;

    public Mono<UserResponseDto> getUserInfo(Long userId) {
        String cacheKey = "user:" + userId;

        return redisTemplate.opsForValue().get(cacheKey)
                .switchIfEmpty(
                        userWebClient.getUserInfo(userId)
                                .flatMap(userInfo -> redisTemplate.opsForValue()
                                        .set(cacheKey, userInfo, Duration.ofMinutes(10))
                                        .thenReturn(userInfo)
                                )
                );
    }
}
