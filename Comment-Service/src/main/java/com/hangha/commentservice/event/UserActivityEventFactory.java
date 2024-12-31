package com.hangha.commentservice.event;

import com.hangha.commentservice.application.command.CommentCommand;
import com.hangha.commentservice.application.command.CommentEventResult;
import com.hangha.commentservice.application.command.CommentUpdateCommand;
import com.hangha.common.event.model.UserActivityEvent;

import java.util.Map;

public class UserActivityEventFactory {

    // 댓글 작성
    public static UserActivityEvent createCommentCreateEvent(Long userId, CommentEventResult eventResult, CommentCommand command) {
        return new UserActivityEvent(
                userId,
                "Comment_CREATE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", command.postId(), "content", command.content())
        );
    }

    // 대댓글 작성
    public static UserActivityEvent createReplyCreateEvent(Long userId, CommentEventResult eventResult, CommentCommand command) {
        return new UserActivityEvent(
                userId,
                "Reply_CREATE",
                eventResult.commentId(),
                "COMMENT",
                Map.of(
                        "PostId", command.postId(),
                        "content", command.content(),
                        "parentId", eventResult.parentId()
                )
        );
    }

    // 댓글 수정
    public static UserActivityEvent createCommentUpdateEvent(Long userId, CommentEventResult eventResult, CommentUpdateCommand command) {
        return new UserActivityEvent(
                userId,
                "Comment_UPDATE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", command.postId(), "content", command.content())
        );
    }

    // 대댓글 수정
    public static UserActivityEvent createReplyUpdateEvent(Long userId,CommentEventResult eventResult, CommentUpdateCommand command) {
        return new UserActivityEvent(
                userId,
                "Reply_UPDATE",
                eventResult.commentId(),
                "COMMENT",
                Map.of(
                        "PostId", command.postId(),
                        "content", command.content(),
                        "parentId", eventResult.parentId()
                )
        );
    }

    // 댓글 삭제
    public static UserActivityEvent createCommentDeleteEvent(Long userId, CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Comment_DELETE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", postId)
        );
    }

    // 대댓글 삭제
    public static UserActivityEvent createReplyDeleteEvent(Long userId, CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Reply_DELETE",
                eventResult.commentId(),
                "COMMENT",
                Map.of(
                        "PostId", postId,
                        "parentId", eventResult.parentId()
                )
        );
    }

    // 댓글 좋아요
    public static UserActivityEvent createCommentLikeEvent(Long userId, CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Comment_LIKE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", postId, "like", true)
        );
    }

    // 댓글 좋아요 취소
    public static UserActivityEvent createCommentUnlikeEvent(Long userId, CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Comment_UNLIKE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", postId, "like", false)
        );
    }

    // 대댓글 좋아요
    public static UserActivityEvent createReplyLikeEvent(Long userId,CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Reply_LIKE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", postId,"ParentId",eventResult.parentId(), "like", true)
        );
    }

    // 대댓글 좋아요 취소
    public static UserActivityEvent createReplyUnlikeEvent(Long userId, CommentEventResult eventResult, Long postId) {
        return new UserActivityEvent(
                userId,
                "Reply_UNLIKE",
                eventResult.commentId(),
                "COMMENT",
                Map.of("PostId", postId,"ParentId",eventResult.parentId(), "like", false)
        );
    }
}
