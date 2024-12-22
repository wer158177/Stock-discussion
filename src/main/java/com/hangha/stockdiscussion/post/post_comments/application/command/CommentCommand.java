package com.hangha.stockdiscussion.post.post_comments.application.command;

public record CommentCommand(
        Long userId,
        Long postId,
        Long parentId,
        String content
) {
    public boolean isReply() {
        return parentId != null;
    }
}
