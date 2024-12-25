package com.hangha.domain.Service;

import com.hangha.controller.dto.LoginRequest;
import com.hangha.controller.dto.LoginResponse;
import com.hangha.domain.entity.User;
import com.hangha.domain.repository.UserRepository;
import com.hangha.jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserInfoService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse getUserByEmail(String email) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

//        // 계정 활성화 여부 확인
//        if (!user.isActive()) {
//            throw new IllegalArgumentException("계정이 비활성화되었습니다.");
//        }


        // 4. 로그인 응답 반환
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().name(),
                true
        );
    }
}
