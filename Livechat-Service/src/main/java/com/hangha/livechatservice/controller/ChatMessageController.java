package com.hangha.livechatservice.controller;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import com.hangha.livechatservice.domain.entity.MessageRoom;
import com.hangha.livechatservice.domain.repository.ChatMessageRepository;
import com.hangha.livechatservice.domain.repository.MessageRoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-messages")
public class ChatMessageController {
    private final ChatMessageRepository messageRepository;
    private final MessageRoomRepository roomRepository;

    public ChatMessageController(ChatMessageRepository messageRepository, MessageRoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendMessageRequest request) {
        MessageRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        ChatMessage message = new ChatMessage();
        message.setMessageRoom(room);
        message.setSenderId(request.getSenderId());
        message.setMessage(request.getMessage());
        messageRepository.save(message);

        return ResponseEntity.ok(message);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(messageRepository.findByMessageRoomId(roomId));
    }
}
