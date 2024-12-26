package com.hangha.postservice.application.command;


public record PostUpdateCommand(
        Long userId,
        Long postId,
        String content,
        String title
) {}



