package com.hangha.postservice.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "post_id", nullable = false)
    private Long postId; // 게시글 ID

    @Column(name = "score", nullable = false)
    private Double score; // 추천 점수

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 추천 점수 업데이트 시점

    @Builder
    public Recommendation(Long userId, Long postId, Double score, LocalDateTime updatedAt) {
        this.userId = userId;
        this.postId = postId;
        this.score = score;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
}
