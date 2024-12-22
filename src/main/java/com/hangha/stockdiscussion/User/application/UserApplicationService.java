package com.hangha.stockdiscussion.User.application;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.application.command.UpdateProfileCommand;
import com.hangha.stockdiscussion.User.domain.Service.ProfileService;
import com.hangha.stockdiscussion.User.domain.Service.UserRegisterService;
import com.hangha.stockdiscussion.User.infrastructure.fileupload.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserApplicationService {

    private final UserRegisterService userRegisterService;
    private final FileUploadService fileUploadService;
    private final ProfileService profileService;

    public UserApplicationService(UserRegisterService userRegisterService, FileUploadService fileUploadService, ProfileService profileService) {
        this.userRegisterService = userRegisterService;
        this.fileUploadService = fileUploadService;
        this.profileService = profileService;
    }

    public void registerUser(RegisterUserCommand command, MultipartFile imageFile) {
        // 1. 회원 중복 체크
        if (userRegisterService.isUserExists(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 파일 업로드 처리
        String imageUrl = (imageFile != null && !imageFile.isEmpty())
                ? fileUploadService.uploadFile(imageFile)
                : null;

        // 3. 회원가입 처리
        RegisterUserCommand updatedCommand = new RegisterUserCommand(
                command.username(),
                command.email(),
                command.password(),
                command.intro(),
                imageUrl,
                command.admin(),
                command.adminToken()
        );

        userRegisterService.registerUser(updatedCommand);
    }


    public void updateProfile(Long userId, UpdateProfileCommand command, MultipartFile profileImageFile) {
        // 프로필 이미지 업로드 처리
        String profileImageUrl = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            profileImageUrl = fileUploadService.uploadFile(profileImageFile);
        }

        // 도메인 서비스 호출
        profileService.updateProfile(userId, command.username(), command.intro(), profileImageUrl);
    }



}