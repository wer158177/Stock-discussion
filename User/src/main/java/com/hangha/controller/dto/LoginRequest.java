package com.hangha.controller.dto;

public record LoginRequest(
        String email,
        String password
) {
    public LoginRequest(String email ,String password ) {
        this.email = email;
        this.password = password;
    }
}

