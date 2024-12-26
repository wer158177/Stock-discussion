package com.hangha.userservice.infrastructure.security.controller;



import com.hangha.userservice.domain.entity.UserRoleEnum;
import com.hangha.userservice.infrastructure.security.jwt.JwtUtil;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 리프레시 토큰을 요청에서 추출
        String refreshToken = jwtUtil.getRefreshTokenFromRequest(request);

        // "Bearer " 접두어를 제거한 후 처리
        if (refreshToken != null) {
            refreshToken = jwtUtil.substringToken(refreshToken); // Bearer 제거

            if (jwtUtil.validateRefreshToken(refreshToken)) {
                // 리프레시 토큰에서 사용자 정보 추출
                Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
                String email = claims.getSubject();  // 클레임에서 email 추출
                Long userId = (Long) claims.get("userId");
                String username = claims.get("username").toString();  // username 추출
                String roleString = claims.get("auth").toString();  // "auth" 클레임에서 역할 추출
                UserRoleEnum role = UserRoleEnum.valueOf(roleString);  // UserRoleEnum으로 변환

                // 새로운 액세스 토큰 생성
                String newAccessToken = jwtUtil.createToken(userId,email,username, role);  // 새 액세스 토큰 생성

                // 새 액세스 토큰 쿠키에 저장
                jwtUtil.addJwtToCookie(newAccessToken, response);


                // 새 액세스 토큰 발급 완료 메시지 반환
                return ResponseEntity.ok("Access Token 재발급 완료");
            } else {
                // 리프레시 토큰이 유효하지 않거나 만료된 경우
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않거나 만료되었습니다. 다시 로그인 해주세요.");
            }
        } else {
            // 리프레시 토큰이 없을 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 없습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 현재 로그인된 사용자의 이메일을 가져옵니다
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getEmail();

        // 모든 디바이스에서 로그아웃 처리
        refreshTokenService.deleteRefreshToken(email);

        // 로그아웃 성공 메시지 반환
        return ResponseEntity.ok("모든 기기에서 로그아웃되었습니다.");
    }




}
