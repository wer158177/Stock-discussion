package com.hangha.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
@Table(name = "post_status")
public class PostStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private int likesCount; // 좋아요 수
    private int commentsCount; // 댓글 수
    private int viewsCount; // 조회수

    // 기본 생성자
    protected PostStatus() {}

    // 생성자
    public PostStatus(Post post) {
        this.post = post;
        this.likesCount = 0;
        this.commentsCount = 0;
        this.viewsCount = 0;
    }

    // 좋아요 수 증가/감소
    public void incrementLikesCount() {
        this.likesCount++;
    }

    public void decrementLikesCount() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    // 댓글 수 증가/감소
    public void incrementCommentsCount() {
        this.commentsCount++;
    }

    public void decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }

    // 조회수 증가
    public void incrementViewsCount() {
        this.viewsCount++;
    }
}

