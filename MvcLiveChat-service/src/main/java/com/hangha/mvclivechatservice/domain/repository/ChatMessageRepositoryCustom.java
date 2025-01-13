package com.hangha.mvclivechatservice.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatMessageRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveChatMessage(String chatRoomName, String senderName, String senderProfileUrl, String content) {
        String sql = "INSERT INTO chat_message (chat_room_name, sender_name, sender_profile_url, content) " +
                "VALUES (?, ?, ?, ?)";

        // SQL 쿼리 실행
        jdbcTemplate.update(sql, chatRoomName, senderName, senderProfileUrl, content);
    }
}
