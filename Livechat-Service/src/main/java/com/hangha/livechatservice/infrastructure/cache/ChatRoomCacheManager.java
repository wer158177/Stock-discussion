package com.hangha.livechatservice.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.livechatservice.domain.entity.ChatRoom;
import com.hangha.livechatservice.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatRoomCacheManager {
    private static final String CHATROOM_KEY_PREFIX = "chatroom:";
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;

    public Mono<Boolean> cacheChatRoom(ChatRoom chatRoom) {
        String key = CHATROOM_KEY_PREFIX + chatRoom.getRoomName();
        try {
            String value = objectMapper.writeValueAsString(chatRoom);

            return reactiveRedisTemplate.opsForValue()
                    .set(key, value)
                    .then(reactiveRedisTemplate.expire(key, java.time.Duration.ofMinutes(30)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<ChatRoom> getChatRoom(String roomName) {
        String key = CHATROOM_KEY_PREFIX + roomName;
        return reactiveRedisTemplate.opsForValue()
                .get(key)
                .flatMap(cachedValue -> {
                    try {
                        ChatRoom chatRoom = objectMapper.readValue(cachedValue, ChatRoom.class);
                        return Mono.just(chatRoom);
                    } catch (JsonProcessingException e) {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(
                        chatRoomRepository.findByRoomName(roomName)
                                .flatMap(chatRoom -> cacheChatRoom(chatRoom).thenReturn(chatRoom))
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomName)))
                );
    }

    public Mono<Void> deleteChatRoom(String roomName) {
        String key = CHATROOM_KEY_PREFIX + roomName;
        return reactiveRedisTemplate.opsForValue()
                .delete(key)
                .then();
    }
}
