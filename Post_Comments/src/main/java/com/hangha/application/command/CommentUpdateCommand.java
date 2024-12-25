package com.hangha.application.command;

public record CommentUpdateCommand(
        Long userId,
        Long commentId,
        Long postId,
        Long parentId,
        String content
) {
    public boolean isReply() {
        return parentId != null;
    }
}

