package com.hangha.application;


import com.hangha.application.command.UpdateProfileCommand;
import com.hangha.controller.dto.*;
import com.hangha.domain.Service.*;
import com.hangha.application.command.RegisterUserCommand;

import com.hangha.emalisender.EmailVerificationService;
import com.hangha.fileupload.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserApplicationService {

    private final UserRegisterService userRegisterService;
    private final FileUploadService fileUploadService;
    private final ProfileService profileService;
    private final PasswordService passwordService;
    private final EmailVerificationService emailVerificationService;
    private final UserLoginService userLoginService;
    private final UserInfoService userInfoService;

    public UserApplicationService(UserRegisterService userRegisterService, FileUploadService fileUploadService, ProfileService profileService, PasswordService passwordService, EmailVerificationService emailVerificationService, UserLoginService userLoginService, UserInfoService userInfoService) {
        this.userRegisterService = userRegisterService;
        this.fileUploadService = fileUploadService;
        this.profileService = profileService;
        this.passwordService = passwordService;
        this.emailVerificationService = emailVerificationService;
        this.userLoginService = userLoginService;
        this.userInfoService = userInfoService;
    }

    public void registerUser(UserRequest requestDto, MultipartFile imageFile) {

       //파일서비스
        String imageUrl = handleFileUpload(imageFile);

        //불벽객체
        RegisterUserCommand command = requestDto.toCommand(imageUrl);

        Long userId = userRegisterService.registerUser(command);

        emailVerificationService.sendVerificationEmail(command.email(), userId);

    }

    public String handleFileUpload(MultipartFile imageFile) {
        return imageFile != null && !imageFile.isEmpty()
                ? fileUploadService.uploadFile(imageFile)
                : null;
    }


    public void updateProfile(Long userId, UpdateProfileRequest requestDto, MultipartFile profileImageFile) {
        // 프로필 이미지 업로드 처리
        String profileImageUrl = handleFileUpload(profileImageFile);

        UpdateProfileCommand command = requestDto.toCommand(profileImageUrl);

        // 도메인 서비스 호출
        profileService.updateProfile(userId, command.username(), command.intro(), profileImageUrl);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        passwordService.changePassword(userId, oldPassword, newPassword);
    }


    //유저인포 전달
    public LoginResponse getUserInfo(LoginRequest request) {
        return userLoginService.loginUser(request);
    }

    //이메일 인증처리
    public  void verifyEmail(VerificationStatusRequest verificationStatusRequest){
        userRegisterService.verifyEmail(verificationStatusRequest);
    }


    public LoginResponse getUserByEmail(String email) {
        return userInfoService.getUserByEmail(email);
    }
}