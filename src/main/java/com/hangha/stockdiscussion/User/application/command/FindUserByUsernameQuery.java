package com.hangha.stockdiscussion.User.application.command;


public record FindUserByUsernameQuery(String username) {
    public FindUserByUsernameQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("유저 이름은 필수입니다.");
        }
    }
}