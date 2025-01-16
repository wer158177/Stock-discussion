package com.hangha.livechatservice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;

    private String chatRoomName;
    private String senderName;
    private String senderProfileUrl;
    private String content;
    private LocalDateTime createdAt;

    // 모든 필드를 포함한 생성자 추가
    public ChatMessage(String id, String chatRoomName, String senderName, String senderProfileUrl, String content, LocalDateTime createdAt) {
        this.id = id;
        this.chatRoomName = chatRoomName;
        this.senderName = senderName;
        this.senderProfileUrl = senderProfileUrl;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now(); // createdAt이 null이면 현재 시간
    }
}
