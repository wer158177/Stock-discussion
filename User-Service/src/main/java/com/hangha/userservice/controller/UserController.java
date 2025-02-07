package com.hangha.userservice.controller;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.userservice.application.UserApplicationService;

import com.hangha.userservice.controller.dto.FollowUserinfo;
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


    //팔로우를 위한 유저인포
    @GetMapping("/info/{username}")
    public ResponseEntity<FollowUserinfo> followUserinfo(
            @PathVariable String username,
            @RequestHeader("X-Claim-userId") Long userId) {
        FollowUserinfo userinfo = userinfoService.getFollowUserInfo(username, userId);
        return ResponseEntity.ok(userinfo);
    }


}