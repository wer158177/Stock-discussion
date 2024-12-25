package com.hangha.dto;

public record UserResponseDto(
        Long userId,
        String email,
        String username,
        String role,
        boolean isActive
) {}
