package com.hangha.mvclivechatservice.infrastructure.dto;

import com.hangha.mvclivechatservice.domain.entity.ChatMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ChatMessageResponseDto {
    private final String type;
    private final String roomName;
    private final Sender sender;
    private final String content;
    private final String timestamp;

    @Getter
    @RequiredArgsConstructor
    public static class Sender {
        private final String name;
        private final String profileUrl;
    }

    // 생성자 또는 Builder를 통해 DTO를 쉽게 생성
    public ChatMessageResponseDto(ChatMessage chatMessage) {
        this.type = "MESSAGE";
        this.roomName = chatMessage.getChatRoom().getName();
        this.sender = new Sender(chatMessage.getSenderName(), chatMessage.getSenderProfileUrl());
        this.content = chatMessage.getContent();
        this.timestamp = chatMessage.getChatRoom().getCreatedAt().toString(); // ISO-8601 형식
    }
}
