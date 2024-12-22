package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.domain.Service.PasswordService;
import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.infrastructure.security.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito를 사용하여 단위 테스트를 지원
class PasswordServiceTest {

    @Mock
    private UserRepository userRepository; // UserRepository를 Mock으로 주입 (데이터베이스 의존성 제거)

    @Mock
    private PasswordEncoder passwordEncoder; // PasswordEncoder를 Mock으로 주입 (암호화 의존성 제거)

    @Mock
    private RefreshTokenService refreshTokenService; // RefreshTokenService를 Mock으로 주입 (토큰 삭제 로직의 독립성 확보)

    @InjectMocks
    private PasswordService passwordService; // PasswordService에 Mock 주입된 객체를 사용하여 실제 테스트

    @Test
    void changePassword_success() {
        // Given: 테스트를 위한 준비 단계
        Long userId = 1L; // 테스트 대상 사용자 ID
        User user = User.builder() // 사용자 엔티티 빌드
                .id(userId)
                .email("test@example.com")
                .password("encodedOldPassword") // 기존 비밀번호(암호화된 값)
                .build();

        // Mock 동작 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // 사용자 조회 성공 시 반환값 설정
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true); // 기존 비밀번호 검증 성공
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword"); // 새 비밀번호 암호화 결과 설정

        // When: 실제 테스트 실행
        passwordService.changePassword(userId, "oldPassword", "newPassword");

        // Then: 결과 검증
        verify(refreshTokenService).deleteRefreshToken(user.getEmail()); // RefreshTokenService가 호출되었는지 확인
        assertEquals("encodedNewPassword", user.getPassword()); // 새 비밀번호가 업데이트되었는지 확인
    }
}
