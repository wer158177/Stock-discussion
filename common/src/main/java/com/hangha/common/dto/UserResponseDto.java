package com.hangha.common.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class  UserResponseDto {

    private Long userId;
    private String username;
    private String imageUrl;

    public UserResponseDto(Long userId, String username, String imageUrl) {
        this.userId = userId;
        this.username = username;
        this.imageUrl = imageUrl;
    }

}
