package com.hangha.userservice.infrastructure.security.service;


import com.hangha.userservice.infrastructure.security.domain.RefreshToken;

import com.hangha.userservice.infrastructure.security.repository.RefreshTokenRepository;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Optional;


@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // 리프레시 토큰 저장
    public void saveRefreshToken(Long userId, String refreshToken, Date tokenExpiration) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 새로운 리프레시 토큰 엔티티 생성
        RefreshToken token = new RefreshToken(user, refreshToken,tokenExpiration, new Date());
        refreshTokenRepository.save(token);
    }

    // 리프레시 토큰 조회
    public Optional<RefreshToken> getRefreshTokenByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    // 리프레시 토큰 삭제
    @Transactional
    public void deleteRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        refreshTokenRepository.deleteByUserId(user.getId());
    }
}
