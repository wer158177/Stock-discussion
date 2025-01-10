package com.hangha.livechatservice.controller.dto.response;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private String senderId;
    private String message;
    private LocalDateTime timestamp;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getMessageRoom().getRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getMessage(),
                chatMessage.getTimestamp()
        );
    }
}