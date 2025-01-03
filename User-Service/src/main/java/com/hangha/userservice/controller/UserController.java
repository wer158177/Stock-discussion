package com.hangha.userservice.controller;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.userservice.application.UserApplicationService;

import com.hangha.userservice.controller.dto.UpdateProfileRequest;
import com.hangha.userservice.controller.dto.UserRequest;

import com.hangha.userservice.domain.Service.UserinfoService;
import com.hangha.userservice.infrastructure.emalisender.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/user")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final EmailVerificationService emailVerificationService;
    private final UserinfoService userinfoService;

    public UserController(UserApplicationService userApplicationService, EmailVerificationService emailVerificationService, UserinfoService userinfoService) {
        this.userApplicationService = userApplicationService;
        this.emailVerificationService = emailVerificationService;
        this.userinfoService = userinfoService;
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerUser(
            @ModelAttribute UserRequest requestDto) {
        // 회원가입 서비스 호출 (중복 체크 및 파일 업로드 처리 포함)
        userApplicationService.registerUser(requestDto, requestDto.getImageFile());
        return ResponseEntity.ok("회원가입 성공!");
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
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


    //유저이름 가져오는 api
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> userinfo(@PathVariable Long userId){
        UserResponseDto userinfo =userinfoService.getUserInfo(userId);
        return ResponseEntity.ok(userinfo);
    }





}