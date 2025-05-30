package com.hangha.commentservice.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCountRequest {
    private  Long postId;
    private  boolean isIncrement;

    public CommentCountRequest(Long postId, boolean isIncrement) {
        this.postId = postId;
        this.isIncrement = isIncrement;
    }
}