package com.hangha.application;

import com.hangha.domain.Service.FollowingService;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class FollowingApplicationService {

    private final FollowingService followingService;

    public FollowingApplicationService(FollowingService followingService) {
        this.followingService = followingService;
    }

    public void followUser(Long followerId, Long followingId) {
        followingService.follow(followerId, followingId);
    }

    public void unfollowUser(Long followerId, Long followingId) {
        followingService.unfollow(followerId, followingId);
    }

    public List<Long> getFollowers(Long userId) {
        return followingService.getFollowers(userId);
    }

    public List<Long> getFollowing(Long userId) {
        return followingService.getFollowing(userId);
    }
}

