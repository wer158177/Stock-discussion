package com.hangha.commentservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comment_likes")
public class CommentLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private PostComments comment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected CommentLikes() {}

    public CommentLikes(PostComments comment, Long userId) {
        this.comment = comment;
        this.userId = userId;
    }

}
