package com.hangha.livechatservice.config;


import com.hangha.common.dto.UserResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, UserResponseDto> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<UserResponseDto> serializer = new Jackson2JsonRedisSerializer<>(UserResponseDto.class);
        RedisSerializationContext<String, UserResponseDto> context = RedisSerializationContext
                .<String, UserResponseDto>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }


}

