package com.hangha.stockdiscussion.User.application.command;



public record RegisterUserCommand(
        String username,
        String email,
        String password,
        String intro,
        String imageUrl
) {
    public RegisterUserCommand {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("유저 이름은 필수입니다.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 주소를 입력하세요.");
        }
    }

    // 이미지 URL 업데이트 메서드
    public RegisterUserCommand withImageUrl(String imageUrl) {
        return new RegisterUserCommand(username, email, password, intro, imageUrl);
    }
}
