package com.hangha.controller.dto;

public record CommentCountRequest(
        Long postId,
        boolean isIncrement
) {
}
