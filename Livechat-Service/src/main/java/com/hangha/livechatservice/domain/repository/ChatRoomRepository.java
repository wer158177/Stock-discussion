package com.hangha.livechatservice.domain.repository;

import com.hangha.livechatservice.domain.entity.ChatRoom;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveCrudRepository<ChatRoom, Long> {
    Mono<ChatRoom> findByName(String name);
}
