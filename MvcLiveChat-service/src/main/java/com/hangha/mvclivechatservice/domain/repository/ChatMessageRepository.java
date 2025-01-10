package com.hangha.mvclivechatservice.domain.repository;

import com.hangha.mvclivechatservice.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}

