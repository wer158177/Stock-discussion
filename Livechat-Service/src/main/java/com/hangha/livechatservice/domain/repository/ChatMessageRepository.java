package com.hangha.livechatservice.domain.repository;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 메시지룸의 메시지 조회
    List<ChatMessage> findByMessageRoomRoomId(Long roomId);

    // 메시지룸 ID와 송신자 ID로 메시지 조회
    List<ChatMessage> findByMessageRoomRoomIdAndSenderId(Long roomId, String senderId);

    // 메시지룸별 최신 메시지 조회
    ChatMessage findTopByMessageRoomRoomIdOrderByTimestampDesc(Long roomId);
}
