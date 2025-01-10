package com.hangha.livechatservice.application;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import com.hangha.livechatservice.domain.service.ChatMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class ChatMessageApplicationService {
    private final ChatMessageService chatMessageService;

    public ChatMessageApplicationService(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    public ChatMessage saveMessage(Long roomId, String senderId, String content) {
        return chatMessageService.saveMessage(roomId, senderId, content);
    }

    public List<ChatMessage> getRoomMessages(Long roomId) {
        return chatMessageService.getMessagesByRoom(roomId);
    }
}