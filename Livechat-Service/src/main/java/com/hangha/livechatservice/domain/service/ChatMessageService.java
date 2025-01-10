package com.hangha.livechatservice.domain.service;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import com.hangha.livechatservice.domain.entity.MessageRoom;
import com.hangha.livechatservice.domain.repository.ChatMessageRepository;
import com.hangha.livechatservice.domain.repository.MessageRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final MessageRoomService messageRoomService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, MessageRoomService messageRoomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.messageRoomService = messageRoomService;
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, String senderId, String content) {
        MessageRoom room = messageRoomService.getRoom(roomId);

        ChatMessage message = new ChatMessage();
        message.setMessageRoom(room);
        message.setSenderId(senderId);
        message.setMessage(content);

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoom(Long roomId) {
        return chatMessageRepository.findByMessageRoomRoomId(roomId);
    }

    public ChatMessage getLatestMessage(Long roomId) {
        return chatMessageRepository.findTopByMessageRoomRoomIdOrderByTimestampDesc(roomId);
    }
}
