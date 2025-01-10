package com.hangha.mvclivechatservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message", schema = "chat_service")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String senderName;  // 발신자 이름

    @Column(nullable = true)
    private String senderProfileUrl;  // 발신자 프로필 이미지 URL

    @Column(nullable = false)
    private String content;  // 메시지 내용

    @Builder
    public ChatMessage(ChatRoom chatRoom, String senderName, String senderProfileUrl, String content) {
        this.chatRoom = chatRoom;
        this.senderName = senderName;
        this.senderProfileUrl = senderProfileUrl;
        this.content = content;
    }
}
