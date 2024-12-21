package com.hangha.stockdiscussion.post.application.command;

public record PostWriteCommand(
        Long userId,
        String content,
        String title
) {}
