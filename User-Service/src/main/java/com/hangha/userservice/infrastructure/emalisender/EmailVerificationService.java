package com.hangha.userservice.infrastructure.emalisender;


import com.hangha.userservice.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailSenderService emailSender;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(EmailSenderService emailSender, VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public void sendVerificationEmail(String email, Long userId) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, userId, LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/user/verify?token=" + token;
        emailSender.sendEmail(email, "이메일 인증", "인증 링크: <a href=\"" + verificationLink + "\">클릭하여 인증</a>");
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("인증 토큰이 만료되었습니다.");
        }

        // 인증 상태 업데이트
        userRepository.findById(verificationToken.getUserId())
                .ifPresent(user -> {
                    user.markAsVerified();
                    userRepository.save(user);
                });

        // 인증 토큰 삭제
        tokenRepository.deleteByToken(token);
    }
}

