package com.hangha.postservice.domain.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_tag")
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY) // 태그와 다대일 관계
    @JoinColumn(name = "tag_id", nullable = false)
    private Hashtag hashtag;

    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시점 자동 설정

    @Builder
    public PostHashtag(Long postId, Hashtag hashtag, LocalDateTime createdAt) {
        this.postId = postId;
        this.hashtag = hashtag;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
