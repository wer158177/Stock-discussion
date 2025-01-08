package com.hangha.userservice.infrastructure.security.controller;

import com.hangha.common.jwt.JwtUtil;
import com.hangha.userservice.application.UserApplicationService;
import com.hangha.userservice.controller.dto.UserRequest;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.infrastructure.emalisender.EmailVerificationService;
import com.hangha.userservice.infrastructure.security.dto.LoginRequestDto;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import com.hangha.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserApplicationService userApplicationService;
    private final EmailVerificationService emailVerificationService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("로그인 성공");
    }


    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerUser(
            @ModelAttribute UserRequest requestDto) {
        // 회원가입 서비스 호출 (중복 체크 및 파일 업로드 처리 포함)
        userApplicationService.registerUser(requestDto, requestDto.getImageFile());
        return ResponseEntity.ok("회원가입 성공!");
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증 성공!");
    }


    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getTokenFromRequest(request.getHeader("Authorization"));

        if (refreshToken != null) {
            refreshToken = jwtUtil.substringToken(refreshToken);

            if (jwtUtil.validateToken(refreshToken)) {
                Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String username = claims.get("username", String.class);
                String role = claims.get("role", String.class);

                String newAccessToken = jwtUtil.createToken(userId, email, username, role, true);
                addCookie(response, JwtUtil.ACCESS_TOKEN, newAccessToken);

                return ResponseEntity.ok("Access Token 재발급 완료");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않거나 만료되었습니다. 다시 로그인 해주세요.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 없습니다.");
        }
    }

    private void addCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}