package com.hangha.livechatservice.domain.repository;

import com.hangha.livechatservice.domain.entity.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    // 카테고리 이름으로 메시지룸 검색
    Optional<MessageRoom> findByCategoryName(String categoryName);

    // 메시지룸이 활성화된 목록 조회
    List<MessageRoom> findByIsActiveTrue();
}
