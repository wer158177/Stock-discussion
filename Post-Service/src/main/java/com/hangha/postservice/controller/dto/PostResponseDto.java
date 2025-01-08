package com.hangha.postservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostResponseDto {
    private String id;
    private String username;
    private String userImage;
    private String content;
    private String image;
    private int likes;
    private LocalDateTime timestamp;
    private List<String> hashtags;
    private boolean isLiked;
}