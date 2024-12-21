package com.hangha.stockdiscussion.post.application.command;


public record PostUpdateCommand(
        Long userId,
        Long postId,
        String content,
        String title
) {}



