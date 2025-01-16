package com.hangha.livechatservice.domain.entity;




import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document("chat_room")
public class ChatRoom {

    @Id
    private String id;

    @Field("room_name")
    private String roomName;
    private LocalDateTime createdAt;

    public ChatRoom(String id,String name) {
        this.id = id;
        this.roomName = name;
        this.createdAt = LocalDateTime.now();
    }
}
