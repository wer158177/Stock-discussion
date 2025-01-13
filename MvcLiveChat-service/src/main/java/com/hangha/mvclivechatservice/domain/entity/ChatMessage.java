package com.hangha.mvclivechatservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message", schema = "chat_service")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatRoomName;

    @Column(nullable = false)
    private String senderName;  // 발신자 이름

    @Column(nullable = true)
    private String senderProfileUrl;  // 발신자 프로필 이미지 URL

    @Column(nullable = false)
    private String content;  // 메시지 내용

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(String chatRoomName, String senderName, String senderProfileUrl, String content, LocalDateTime createdAt) {
        this.chatRoomName = chatRoomName;
        this.senderName = senderName;
        this.senderProfileUrl = senderProfileUrl;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();

    }


}
