package com.hangha.stockdiscussion.User.controller;

import com.hangha.stockdiscussion.User.application.UserApplicationService;
import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.common.FileUploadService;
import com.hangha.stockdiscussion.User.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final FileUploadService fileUploadService;



    public UserController(UserApplicationService userApplicationService, FileUploadService fileUploadService) {
        this.userApplicationService = userApplicationService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerUser(
            @ModelAttribute UserRequestDto requestDto) {


        // 파일 업로드 처리
        String uploadedImageUrl = requestDto.getImageFile() != null && !requestDto.getImageFile().isEmpty()
                ? fileUploadService.uploadFile(requestDto.getImageFile())
                : null;

        // DTO 메서드를 사용해 Command 생성
        RegisterUserCommand command = requestDto.toCommand().withImageUrl(uploadedImageUrl);

        // 회원가입 서비스 호출
        userApplicationService.registerUser(command, requestDto.getImageFile());

        return ResponseEntity.ok("회원가입 성공!");
    }
}