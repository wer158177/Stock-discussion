package com.hangha.stockdiscussion.User.application;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.Service.FileUploadService;
import com.hangha.stockdiscussion.User.domain.Service.UserService;
import com.hangha.stockdiscussion.security.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserApplicationService {

    private final UserService userService;
    private final FileUploadService fileUploadService;

    public UserApplicationService(UserService userService, FileUploadService fileUploadService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.fileUploadService = fileUploadService;
    }



    public void registerUser(RegisterUserCommand command, MultipartFile imageFile) {
        // 1. 회원 중복 체크
        if (userService.isUserExists(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }


        //파일 업로드 수정해야함
        String imageUrl = (imageFile != null && !imageFile.isEmpty())
                ? fileUploadService.uploadFile(imageFile)
                : null;

        RegisterUserCommand updatedCommand = new RegisterUserCommand(
                command.username(),
                command.email(),
                command.password(),
                command.intro(),
                imageUrl,
                command.admin(),
                command.adminToken()
        );

        userService.registerUser(updatedCommand);
    }


}
