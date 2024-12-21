package com.hangha.stockdiscussion.post.post_comments.application.command;

public record CommentUpdateCommand(
        Long userId,
        Long commentId,
        Long postId,
        String comment) {
}
