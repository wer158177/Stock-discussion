package com.hangha.userservice.infrastructure.security.controller;

import com.hangha.common.JwtUtil;
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
import org.springframework.http.server.reactive.ServerHttpRequest;

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
        String refreshToken = jwtUtil.getRefreshTokenFromRequest(request);

        if (refreshToken != null) {
            refreshToken = jwtUtil.substringToken(refreshToken);

            if (jwtUtil.validateRefreshToken(refreshToken)) {
                Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String username = claims.get("username", String.class);
                String role = claims.get("auth", String.class);

                String newAccessToken = jwtUtil.createToken(userId, email, username, role);
                jwtUtil.addJwtToCookie(newAccessToken, response);

                return ResponseEntity.ok("Access Token 재발급 완료");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않거나 만료되었습니다. 다시 로그인 해주세요.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 없습니다.");
        }
    }

    @PostMapping("/refresh/reactive")
    public ResponseEntity<String> refreshAccessToken(ServerHttpRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getCookieValue(request, JwtUtil.REFRESH_TOKEN_HEADER);

        if (refreshToken != null) {
            refreshToken = jwtUtil.substringToken(refreshToken);

            if (jwtUtil.validateRefreshToken(refreshToken)) {
                Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String username = claims.get("username", String.class);
                String role = claims.get("auth", String.class);

                String newAccessToken = jwtUtil.createToken(userId, email, username, role);
                jwtUtil.addJwtToCookie(newAccessToken, response);

                return ResponseEntity.ok("Access Token 재발급 완료");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않거나 만료되었습니다. 다시 로그인 해주세요.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 없습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getEmail();

        refreshTokenService.deleteRefreshToken(email);

        return ResponseEntity.ok("모든 기기에서 로그아웃되었습니다.");
    }
}
