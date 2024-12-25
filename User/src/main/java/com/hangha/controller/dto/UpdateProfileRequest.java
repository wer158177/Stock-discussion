package com.hangha.controller.dto;

import com.hangha.application.command.UpdateProfileCommand;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 20, message = "유저 이름은 2자 이상 20자 이하로 입력하세요.")
    private String username;

    @Size(max = 100, message = "자기소개는 최대 100자까지 입력 가능합니다.")
    private String intro;

    private MultipartFile imageFile;

    // Command로 변환
    public UpdateProfileCommand toCommand(String imageFile) {
        return new UpdateProfileCommand(username, intro,imageFile);
    }
}
