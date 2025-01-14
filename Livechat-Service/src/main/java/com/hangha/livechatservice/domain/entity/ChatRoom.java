package com.hangha.livechatservice.domain.entity;




import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("chat_room")
public class ChatRoom {

    @Id
    private Long id;

    private String name;
    private LocalDateTime createdAt;

    public ChatRoom(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
}
