package com.hangha.postservice.application.command;

import java.util.List;

public record PostWriteCommand(
        Long userId,
        String content,
        String title,
        List<String> tags

) {}
