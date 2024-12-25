package com.hangha.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comment_likes")
public class CommentLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_comment_id") // PostComments 엔티티의 ID와 매핑
    private PostComments  postComments;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected CommentLikes() {}

    public CommentLikes(PostComments  postComments, Long userId) {
        this. postComments =  postComments;
        this.userId = userId;
    }

}
