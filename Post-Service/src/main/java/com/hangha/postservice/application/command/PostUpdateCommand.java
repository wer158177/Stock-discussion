package com.hangha.postservice.application.command;


import java.util.List;

public record PostUpdateCommand(
        Long userId,
        Long postId,
        String content,
        String title,
        List<String>tags
) {}



