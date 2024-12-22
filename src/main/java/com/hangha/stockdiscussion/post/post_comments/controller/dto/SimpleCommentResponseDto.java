package com.hangha.stockdiscussion.post.post_comments.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SimpleCommentResponseDto {
    private final Long id;
    private final String content;
    private final Long userId;
    private final LocalDateTime createdAt;
}

