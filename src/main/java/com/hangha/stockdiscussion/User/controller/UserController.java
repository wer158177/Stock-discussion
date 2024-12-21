package com.hangha.stockdiscussion.User.controller;

import com.hangha.stockdiscussion.User.application.UserApplicationService;
import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;

import com.hangha.stockdiscussion.User.controller.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserApplicationService userApplicationService;


    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerUser(
            @ModelAttribute UserRequestDto requestDto) {
        RegisterUserCommand command = requestDto.toCommand();
        // 회원가입 서비스 호출 (중복 체크 및 파일 업로드 처리 포함)
        userApplicationService.registerUser(command, requestDto.getImageFile());
        return ResponseEntity.ok("회원가입 성공!");
    }


}