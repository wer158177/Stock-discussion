package com.hangha.commentservice.application.command;

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
