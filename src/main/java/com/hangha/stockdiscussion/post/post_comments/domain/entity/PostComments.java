package com.hangha.stockdiscussion.post.post_comments.domain.entity;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")  // 게시글과 연결되는 외래키
    private Post post;


    private Long userId;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //테이블 따로관리
    private int likes;



    @Builder
    public PostComments(Long id,Post post, Long userId, String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.post = post;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateComment(String newComment) {
        this.comment = newComment;
    }

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = this.createdAt;
    }

    // 게시글 수정 시 updatedAt만 갱신하는 메서드
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }





}
