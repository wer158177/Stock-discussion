package com.hangha.livechatservice.domain.service;

import com.hangha.livechatservice.domain.entity.MessageRoom;
import com.hangha.livechatservice.domain.repository.MessageRoomRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageRoomService {
    private final MessageRoomRepository messageRoomRepository;

    public MessageRoomService(MessageRoomRepository messageRoomRepository) {
        this.messageRoomRepository = messageRoomRepository;
    }

    @Transactional
    public MessageRoom createRoom(String stockName) {
        MessageRoom room = new MessageRoom();
        room.setStockName(stockName);
        return messageRoomRepository.save(room);
    }

    public List<MessageRoom> getAllActiveRooms() {
        return messageRoomRepository.findByIsActiveTrue();
    }

    public MessageRoom getRoom(Long roomId) {
        return messageRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
    }
}

