package com.hangha.commentservice.domain.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long postId;

    @Column(nullable = true) // 부모 댓글 ID (null이면 일반 댓글)
    private Long parentId;

    private Long userId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 대댓글 리스트 (부모 댓글을 기준으로 관리)
    @OneToMany(mappedBy = "parentId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComments> childComments = new ArrayList<>();

    // 좋아요 리스트
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLikes> likes = new ArrayList<>();



    @Builder
    private PostComments(Long id,Long postId, Long userId,Long parentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostComments createComment(Long postId, Long userId, String content, Long parentId) {
        return PostComments.builder()
                .postId(postId)
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
