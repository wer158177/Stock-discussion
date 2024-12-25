package com.hangha.controller;

import com.hangha.application.UserApplicationService;

import com.hangha.controller.dto.*;
import com.hangha.domain.Service.UserRegisterService;

import com.hangha.dto.UserResponseDto;
import com.hangha.emalisender.EmailVerificationService;
import com.hangha.emalisender.VerificationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final EmailVerificationService emailVerificationService;


    public UserController(UserApplicationService userApplicationService, EmailVerificationService emailVerificationService) {
        this.userApplicationService = userApplicationService;
        this.emailVerificationService = emailVerificationService;

    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerUser(
            @ModelAttribute UserRequest requestDto) {
        // 회원가입 서비스 호출 (중복 체크 및 파일 업로드 처리 포함)
        userApplicationService.registerUser(requestDto, requestDto.getImageFile());
        return ResponseEntity.ok("회원가입 성공!");
    }


    @PostMapping("/verify-status")
    public ResponseEntity<String> verifyEmail(@RequestBody VerificationStatusRequest verificationStatusRequest) {
        userApplicationService.verifyEmail(verificationStatusRequest);
        return ResponseEntity.ok("이메일 인증 성공!");
    }


    @PatchMapping(value = "/{userId}/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long userId,
            @ModelAttribute UpdateProfileRequest requestDto,
            @RequestParam(required = false) MultipartFile profileImageFile) {

        userApplicationService.updateProfile(userId, requestDto, profileImageFile);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userApplicationService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }


    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> getUserByUsername(@RequestBody LoginRequest request) {
        System.out.println("request = " + request.email()+" "+request.password());
        LoginResponse user =  userApplicationService.getUserInfo(request);
        System.out.println("wlswkfh dslkfjaslkdf이름나와라진짜로"+user.email());
        System.out.println("dsfasalkdflaskdfjlk이ㅡㄻ나와라"+user.username());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


    @GetMapping("/{email}")
    public ResponseEntity<LoginResponse> getUserByEmail(@PathVariable String email) {
        LoginResponse user = userApplicationService.getUserByEmail(email);
        System.out.println(email);
        System.out.println(user);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

}