package com.hangha.stockdiscussion.post.domain.entity;


import jakarta.persistence.*;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Post( Long userId,String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.title=title;
        this.content=content;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
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

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }



}
