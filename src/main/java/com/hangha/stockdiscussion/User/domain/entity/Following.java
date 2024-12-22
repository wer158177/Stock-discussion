package com.hangha.stockdiscussion.User.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
