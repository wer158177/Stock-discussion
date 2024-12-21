package com.hangha.stockdiscussion.User.controller.dto;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "유저 이름은 필수입니다.")
    @Size(min = 2, max = 20, message = "유저 이름은 2자 이상 20자 이하로 입력하세요.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    private String intro;

    private MultipartFile imageFile;

    private boolean admin = false;

    private String adminToken = "";



    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, email, password, intro, null,admin,adminToken);
    }
}
