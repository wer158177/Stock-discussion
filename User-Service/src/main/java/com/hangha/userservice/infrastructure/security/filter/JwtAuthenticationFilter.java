package com.hangha.userservice.infrastructure.security.filter;

import com.hangha.common.jwt.JwtUtil;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getUserRole().name(),
                true
        );

        String refreshToken = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getUserRole().name(),
                false
        );

        addCookie(response, JwtUtil.ACCESS_TOKEN, accessToken);
        addCookie(response, JwtUtil.REFRESH_TOKEN, refreshToken);

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken,
                new Date(System.currentTimeMillis() + JwtUtil.REFRESH_TOKEN_EXPIRE_TIME));
    }

    private void addCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}