package com.hangha.userservice.infrastructure.event;

import com.hangha.common.event.model.UserActivityEvent;

import java.util.Map;

public class UserActivityEventFactory {

    public static UserActivityEvent createFollowEvent(Long userId, Long targetUserId) {
        return new UserActivityEvent(
                userId,
                "Follow",
                targetUserId,
                "FOLLOW",
                Map.of("action", "follow")
        );
    }

    public static UserActivityEvent createUnfollowEvent(Long userId, Long targetUserId) {
        return new UserActivityEvent(
                userId,
                "Unfollow",
                targetUserId,
                "FOLLOW",
                Map.of("action", "unfollow")
        );
    }
}


