package com.hangha.application.command;

public record PostWriteCommand(
        Long userId,
        String content,
        String title
) {}
