package com.hangha.livechatservice.controller;

import com.hangha.livechatservice.application.MessageRoomApplicationService;
import com.hangha.livechatservice.domain.entity.MessageRoom;
import com.hangha.livechatservice.domain.repository.MessageRoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message-rooms")

public class MessageRoomController {
    private final MessageRoomApplicationService messageRoomApplicationService;

    public MessageRoomController(MessageRoomApplicationService messageRoomApplicationService) {
        this.messageRoomApplicationService = messageRoomApplicationService;
    }


    @PostMapping
    public ResponseEntity<MessageRoom> createRoom(@RequestBody String stockName) {
        return ResponseEntity.ok(messageRoomApplicationService.createRoom(stockName));
    }

    @GetMapping
    public ResponseEntity<List<MessageRoom>> getActiveRooms() {
        return ResponseEntity.ok(messageRoomApplicationService.getActiveRooms());
    }
}

