package com.hangha.mvclivechatservice.infrastructure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IncomingMessage {
    private String type;        // MESSAGE, EVENT 등 메시지 타입
    private String content;     // 메시지 내용
    private String roomName;    // 방 이름
    private String sender;      // 발신자 이름
}