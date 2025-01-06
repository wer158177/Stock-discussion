package com.hangha.postservice.infrastructure.event;

import com.hangha.common.event.model.UserActivityEvent;
import com.hangha.postservice.controller.dto.PostRequestDto;


import java.util.Map;

public class UserActivityEventFactory {

    public static UserActivityEvent createPostCreateEvent(Long userId,Long postId, PostRequestDto postRequestDto) {
        return new UserActivityEvent(
                userId,
                "POST_CREATE",
                postId,
                "POST",
                Map.of("title", postRequestDto.getTitle(), "content", postRequestDto.getContent())
        );
    }

    public static UserActivityEvent createPostUpdateEvent(Long userId, Long postId, PostRequestDto postRequestDto) {
        return new UserActivityEvent(
                userId,
                "POST_UPDATE",
                postId,
                "POST",
                Map.of("title", postRequestDto.getTitle(), "content", postRequestDto.getContent())
        );
    }

    public static UserActivityEvent createPostDeleteEvent(Long userId, Long postId) {
        return new UserActivityEvent(
                userId,
                "POST_DELETE",
                postId,
                "POST",
                null
        );
    }

    public static UserActivityEvent createLikeEvent(Long userId, Long postId) {
        return new UserActivityEvent(
                userId,
                "POST_LIKE",
                postId,
                "POST",
                Map.of("like",true)
        );
    }

    public static UserActivityEvent createUnlikeEvent(Long userId, Long postId) {
        return new UserActivityEvent(
                userId,
                "POST_UNLIKE",
                postId,
                "POST",
                Map.of("like",false)
        );
    }

}
