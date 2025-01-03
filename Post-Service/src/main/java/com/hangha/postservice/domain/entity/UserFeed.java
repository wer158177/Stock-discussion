package com.hangha.postservice.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_feed")
public class UserFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "post_id", nullable = false)
    private Long postId; // 게시글 ID

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Builder
    public UserFeed(Long userId, Long postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
