package com.hangha.postservice.application.command;

public record PostWriteCommand(
        Long userId,
        String content,
        String title
) {}
