package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.infrastructure.security.service.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public PasswordService(PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        refreshTokenService.deleteRefreshToken(user.getEmail());
    }
}
