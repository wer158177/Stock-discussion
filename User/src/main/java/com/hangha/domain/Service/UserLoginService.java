package com.hangha.domain.Service;


import com.hangha.controller.dto.LoginRequest;
import com.hangha.controller.dto.LoginResponse;
import com.hangha.domain.entity.User;
import com.hangha.domain.repository.UserRepository;
import com.hangha.jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse loginUser(LoginRequest request) {
        // 1. 유저 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }


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
