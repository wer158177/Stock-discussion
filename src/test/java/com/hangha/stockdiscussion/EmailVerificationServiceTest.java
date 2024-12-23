package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.infrastructure.emalisender.EmailSenderService;
import com.hangha.stockdiscussion.User.infrastructure.emalisender.EmailVerificationService;
import com.hangha.stockdiscussion.User.infrastructure.emalisender.VerificationToken;
import com.hangha.stockdiscussion.User.infrastructure.emalisender.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailSenderService emailSender;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    void shouldGenerateAndSaveVerificationToken() {
        // Given
        String email = "test@example.com";
        Long userId = 1L;

        // When
        emailVerificationService.sendVerificationEmail(email, userId);

        // Then
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailSender, times(1)).sendEmail(eq(email), eq("이메일 인증"), contains("http://localhost:8080/api/user/verify?token="));
    }

    @Test
    void shouldThrowExceptionForExpiredToken() {
        // Given
        VerificationToken expiredToken = new VerificationToken("token", 1L, LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(expiredToken));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailVerificationService.verifyEmail("token");
        });
        assertEquals("인증 토큰이 만료되었습니다.", exception.getMessage());
    }

    @Test
    void shouldActivateUserOnValidToken() {
        // Given
        VerificationToken validToken = new VerificationToken("token", 1L, LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(validToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "test@example.com", false))); // 기본 User 객체 생성

        // When
        emailVerificationService.verifyEmail("token");

        // Then
        verify(userRepository, times(1)).save(argThat(user -> user.isVerified())); // 사용자 활성화 확인
        verify(tokenRepository, times(1)).deleteByToken("token");
    }
}
