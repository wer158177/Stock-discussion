package com.hangha.userservice.infrastructure.security.repository;

import com.hangha.userservice.infrastructure.security.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    // 사용자 ID로 리프레시 토큰 조회
    Optional<RefreshToken> findByUserId(Long userId);

    // 사용자 ID로 리프레시 토큰 삭제
    void deleteByUserId(Long userId);
}
