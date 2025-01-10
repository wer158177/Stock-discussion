package com.hangha.livechatservice.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageRequest {
    private Long roomId;
    private String senderId;
    private String message;
}