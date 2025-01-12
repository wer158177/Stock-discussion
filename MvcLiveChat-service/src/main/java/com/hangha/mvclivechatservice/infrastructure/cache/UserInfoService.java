package com.hangha.mvclivechatservice.infrastructure.cache;



import com.hangha.common.dto.UserResponseDto;
import com.hangha.mvclivechatservice.infrastructure.client.UserFeignClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserInfoService {
    private final UserFeignClient userFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserInfoService(UserFeignClient userFeignClient, RedisTemplate<String, Object> redisTemplate) {
        this.userFeignClient = userFeignClient;
        this.redisTemplate = redisTemplate;
    }

    public UserResponseDto getUserInfo(Long userId) {
        String cacheKey = "user:" + userId;

        // 캐시에서 데이터 조회
        UserResponseDto cachedUser = (UserResponseDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        // 캐시에 데이터가 없으면 Feign 클라이언트로 유저 서버 호출
        UserResponseDto userInfo = userFeignClient.getUserInfo(userId);

        // 캐시에 저장
        if (userInfo != null) {
            redisTemplate.opsForValue().set(cacheKey, userInfo, Duration.ofMinutes(10));
        }

        return userInfo;
    }
}
