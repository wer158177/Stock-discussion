package com.hangha.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "following")
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long followerId;  // 팔로우하는 사용자
    private Long followingId; // 팔로우 당하는 사용자

    public Following(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }


}
