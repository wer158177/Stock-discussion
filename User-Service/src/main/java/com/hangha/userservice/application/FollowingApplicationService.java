package com.hangha.userservice.application;

import com.hangha.common.event.model.UserActivityEvent;
import com.hangha.userservice.domain.Service.FollowingService;
import com.hangha.userservice.infrastructure.event.UserActivityEventFactory;
import com.hangha.userservice.infrastructure.event.UserActivityProducer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class FollowingApplicationService {

    private final FollowingService followingService;
    private final UserActivityProducer userActivityProducer;

    public FollowingApplicationService(FollowingService followingService, UserActivityProducer userActivityProducer) {
        this.followingService = followingService;
        this.userActivityProducer = userActivityProducer;
    }

    public void followUser(Long followerId, Long followingId) {
        followingService.follow(followerId, followingId);
        // 팔로우 이벤트 생성 및 전송
        UserActivityEvent followEvent = UserActivityEventFactory.createFollowEvent(followerId, followingId);
        userActivityProducer.sendActivityEvent(followEvent);
    }

    public void unfollowUser(Long followerId, Long followingId) {
        followingService.unfollow(followerId, followingId);
        // 언팔로우 이벤트 생성 및 전송
        UserActivityEvent unfollowEvent = UserActivityEventFactory.createUnfollowEvent(followerId, followingId);
        userActivityProducer.sendActivityEvent(unfollowEvent);
    }

    public List<Long> getFollowers(Long userId, Long cursor, int size) {
        return followingService.getFollowers(userId, cursor, size);
    }

    public List<Long> getFollowing(Long userId) {
        return followingService.getFollowing(userId);
    }
}

