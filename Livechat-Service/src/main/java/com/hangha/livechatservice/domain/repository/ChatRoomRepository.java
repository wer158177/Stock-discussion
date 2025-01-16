package com.hangha.livechatservice.domain.repository;

import com.hangha.livechatservice.domain.entity.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    Mono<ChatRoom> findByRoomName(String RoomName);
}
