package com.hangha.postservice.domain.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "post_likes")
public class PostLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long userId;



    protected PostLikes() {}

    public PostLikes(Post post, Long userId) {
        this.post = post;
        this.userId = userId;
    }
}
