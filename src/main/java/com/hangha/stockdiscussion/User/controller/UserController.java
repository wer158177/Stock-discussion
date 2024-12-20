package com.hangha.stockdiscussion.User.controller;

import com.hangha.stockdiscussion.User.application.UserApplicationService;
import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;

import com.hangha.stockdiscussion.User.dto.LoginRequestDto;
import com.hangha.stockdiscussion.User.dto.UserRequestDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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

    @PostMapping("/user/login")
    public String login(@RequestBody LoginRequestDto requestDto,HttpServletResponse res) {
        try {
            if (userApplicationService.login(requestDto,res)) {
                return "메인페이지로가셈";
            }
        } catch (IllegalArgumentException e) {
            System.out.println("에러 메시지: " + e.getMessage());
            return "로그인실패했으니 다시로그인페이지";
        }
        return "로그인 실패";
    }


}