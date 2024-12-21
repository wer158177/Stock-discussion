package com.hangha.stockdiscussion.post.post_comments.application.command;

public record CommentCommand(
        Long userId,
        Long postId,
        String comment)
{ }
