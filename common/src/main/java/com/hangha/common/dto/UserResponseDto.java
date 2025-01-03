package com.hangha.common.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String username;

    public UserResponseDto(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

}
