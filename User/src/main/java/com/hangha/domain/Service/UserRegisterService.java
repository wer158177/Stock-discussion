package com.hangha.domain.Service;

import com.hangha.controller.dto.VerificationStatusRequest;
import com.hangha.domain.entity.User;
import com.hangha.domain.repository.UserRepository;
import com.hangha.application.command.RegisterUserCommand;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final String ADMIN_TOKEN = "990226";

    @Transactional
    public Long registerUser(RegisterUserCommand command) {
        // 1. 회원 중복 체크
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.createUser(command, encodedPassword);
        userRepository.save(user);

        return user.getId();
    }


    //이메일인증
    public void verifyEmail(VerificationStatusRequest verificationStatusRequest) {
        User user = userRepository.findById(verificationStatusRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저 ID입니다."));
        user.markAsVerified(verificationStatusRequest.isVerified()); // 인증 상태 변경
        userRepository.save(user);
    }


}
