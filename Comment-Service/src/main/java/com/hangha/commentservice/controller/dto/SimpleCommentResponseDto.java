package com.hangha.commentservice.controller.dto;

import com.hangha.commentservice.domain.entity.PostComments;
import com.hangha.common.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SimpleCommentResponseDto {
    private Long id;
    private String username;
    private String content;
    private String userImage;
    private LocalDateTime createdAt;
    private int likeCount;
    private boolean isLiked;
    private List<SimpleCommentResponseDto> replies;

    public SimpleCommentResponseDto(PostComments comment, UserResponseDto userResponseDto, boolean isLiked, List<SimpleCommentResponseDto> replies) {
        this.id = comment.getId();
        this.username = userResponseDto.getUsername();
        this.content = comment.getContent();
        this.userImage = userResponseDto.getImageUrl();
        this.createdAt = comment.getCreatedAt();
        this.likeCount = comment.getLikes().size();
        this.isLiked = isLiked;
        this.replies = replies;
    }
}