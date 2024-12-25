package com.hangha.emalisender;


import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailSenderService emailSender;
    private final VerificationTokenRepository tokenRepository;
    private final RestTemplate restTemplate;

    public EmailVerificationService(EmailSenderService emailSender, VerificationTokenRepository tokenRepository, RestTemplate restTemplate) {
        this.emailSender = emailSender;
        this.tokenRepository = tokenRepository;
        this.restTemplate = restTemplate;
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
        // 토큰 조회
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));

        // 토큰 만료 여부 확인
        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("인증 토큰이 만료되었습니다.");
        }

        // User 모듈의 인증 상태 변경 API 호출
        String url = "http://localhost:8082/api/user/verify-status"; // User 모듈의 API 엔드포인트
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 생성
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("userId", verificationToken.getUserId());
        requestPayload.put("isVerified", true); // 인증 상태

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

        // REST API 호출
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("유저 상태를 업데이트할 수 없습니다.");
        }

        // 인증 토큰 삭제
        tokenRepository.deleteByToken(token);
    }
}

