package com.hangha.userservice.application;

import com.hangha.userservice.application.command.RegisterUserCommand;
import com.hangha.userservice.application.command.UpdateProfileCommand;
import com.hangha.userservice.controller.dto.UpdateProfileRequest;
import com.hangha.userservice.controller.dto.UserRequest;
import com.hangha.userservice.domain.Service.PasswordService;
import com.hangha.userservice.domain.Service.ProfileService;
import com.hangha.userservice.domain.Service.UserRegisterService;


import com.hangha.userservice.infrastructure.emalisender.EmailVerificationService;
import com.hangha.userservice.infrastructure.fileupload.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserApplicationService {

    private final UserRegisterService userRegisterService;
    private final FileUploadService fileUploadService;
    private final ProfileService profileService;
    private final PasswordService passwordService;
    private final EmailVerificationService emailVerificationService;

    public UserApplicationService(UserRegisterService userRegisterService, FileUploadService fileUploadService, ProfileService profileService, PasswordService passwordService, EmailVerificationService emailVerificationService) {
        this.userRegisterService = userRegisterService;
        this.fileUploadService = fileUploadService;
        this.profileService = profileService;
        this.passwordService = passwordService;
        this.emailVerificationService = emailVerificationService;
    }

    public void registerUser(UserRequest requestDto, MultipartFile imageFile) {
        String imageUrl = handleFileUpload(imageFile);

        RegisterUserCommand command = requestDto.toCommand(imageUrl);

        Long userId = userRegisterService.registerUser(command);

        emailVerificationService.sendVerificationEmail(command.email(), userId);

    }

    public String handleFileUpload(MultipartFile imageFile) {
      return  imageFile != null && !imageFile.isEmpty()
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

}