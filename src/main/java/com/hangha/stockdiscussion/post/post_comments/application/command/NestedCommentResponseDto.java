package com.hangha.stockdiscussion.post.post_comments.application.command;

import java.util.List;

public record NestedCommentResponseDto(
        Long id,                         // 댓글 ID
        String content,                  // 댓글 내용
        Long parentId,                   // 부모 댓글 ID (null이면 최상위 댓글)
        Long userId,                     // 작성자 ID
        List<NestedCommentResponseDto> replies // 대댓글 리스트
) {}