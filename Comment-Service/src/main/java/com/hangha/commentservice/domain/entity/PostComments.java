package com.hangha.commentservice.domain.entity;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")  // 게시글과 연결되는 외래키
    private Post post;

    @Column(nullable = true) // 부모 댓글 ID (null이면 일반 댓글)
    private Long parentId;

    private Long userId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //테이블 따로관리
    private int likes;



    @Builder
    private PostComments(Long id,Post post, Long userId,Long parentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.post = post;
        this.userId = userId;
        this.parentId = parentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostComments createComment(Post post, Long userId, String content, Long parentId) {
        return PostComments.builder()
                .post(post)
                .userId(userId)
                .content(content)
                .parentId(parentId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }







    public void updateComment(String newContent) {
        this.content = newContent;
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
