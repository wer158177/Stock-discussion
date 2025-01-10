package com.hangha.livechatservice.application;

import com.hangha.livechatservice.domain.entity.MessageRoom;
import com.hangha.livechatservice.domain.service.MessageRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class MessageRoomApplicationService {
    private final MessageRoomService messageRoomService;

    public MessageRoomApplicationService(MessageRoomService messageRoomService) {
        this.messageRoomService = messageRoomService;
    }

    public MessageRoom createRoom(String stockName) {
        return messageRoomService.createRoom(stockName);
    }

    public List<MessageRoom> getActiveRooms() {
        return messageRoomService.getAllActiveRooms();
    }
}