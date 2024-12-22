package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.domain.entity.UserRoleEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final String ADMIN_TOKEN = "990226";

    public Long registerUser(RegisterUserCommand command) {
        // 1. 회원 중복 체크
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }


        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.builder()
                .username(command.username())
                .password(encodedPassword)
                .email(command.email())
                .intro(command.intro())
                .imageUrl(command.imageUrl())
                .userRole(UserRoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .build();
       userRepository.save(user);

        return user.getId();
    }




}
