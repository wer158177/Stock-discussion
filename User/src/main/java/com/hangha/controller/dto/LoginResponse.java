package com.hangha.controller.dto;


public record LoginResponse(
        Long userId,
        String username,
        String email,
        String role,
        boolean isActive
) {}
