package com.hangha.stockdiscussion.User.application;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.common.FileUploadService;
import com.hangha.stockdiscussion.User.domain.Service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserApplicationService {

    private final UserService userService;
    private final FileUploadService fileUploadService;

    public UserApplicationService(UserService userService, FileUploadService fileUploadService) {
        this.userService = userService;
        this.fileUploadService = fileUploadService;
    }

    public void registerUser(RegisterUserCommand command, MultipartFile imageFile) {
        String imageUrl = fileUploadService.uploadFile(imageFile);
        RegisterUserCommand updatedCommand = new RegisterUserCommand(
                command.username(),
                command.email(),
                command.password(),
                command.intro(),
                imageUrl
        );
        userService.registerUser(updatedCommand);
    }
}
