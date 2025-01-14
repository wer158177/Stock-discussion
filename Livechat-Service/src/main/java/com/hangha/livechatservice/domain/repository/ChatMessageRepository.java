package com.hangha.livechatservice.domain.repository;



import com.hangha.livechatservice.domain.entity.ChatMessage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveCrudRepository<ChatMessage, Long> {
    Flux<ChatMessage> findByChatRoomName(String roomName);
}
