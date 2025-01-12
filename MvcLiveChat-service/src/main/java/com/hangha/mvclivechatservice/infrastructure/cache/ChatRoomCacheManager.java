package com.hangha.mvclivechatservice.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.mvclivechatservice.domain.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomCacheManager {
    private static final String CHATROOM_KEY_PREFIX = "chatroom:";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // Redis에 ChatRoom 전체 정보 저장
    public void cacheChatRoom(ChatRoom chatRoom) {
        String key = CHATROOM_KEY_PREFIX + chatRoom.getName();
        try {
            String value = objectMapper.writeValueAsString(chatRoom);

            // 예시: 30분 만료 설정
            redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);

            log.info("[ChatRoomCacheManager] 캐싱 완료: {}", key);
        } catch (JsonProcessingException e) {
            log.error("[ChatRoomCacheManager] 직렬화 실패: {}", e.getMessage());
        }
    }

    // Redis에서 ChatRoom 조회
    public ChatRoom getCachedChatRoom(String roomName) {
        String key = CHATROOM_KEY_PREFIX + roomName;
        String cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(cachedValue, ChatRoom.class);
        } catch (Exception e) {
            log.error("[ChatRoomCacheManager] 역직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    // Redis에서 채팅방 삭제
    public void deleteChatRoom(String roomName) {
        String key = CHATROOM_KEY_PREFIX + roomName;
        redisTemplate.delete(key);
        log.info("[ChatRoomCacheManager] 캐싱 삭제: {}", key);
    }
}