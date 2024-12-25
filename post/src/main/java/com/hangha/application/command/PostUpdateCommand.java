package com.hangha.application.command;


public record PostUpdateCommand(
        Long userId,
        Long postId,
        String content,
        String title
) {}



