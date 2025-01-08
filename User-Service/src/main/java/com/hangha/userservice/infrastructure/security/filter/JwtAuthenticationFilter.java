package com.hangha.userservice.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.common.jwt.JwtUtil;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.infrastructure.security.controller.dto.LoginRequestDto;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import com.hangha.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword());
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
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

        // JWT 토큰 쿠키 추가
        addSecureCookie(response, JwtUtil.ACCESS_TOKEN, accessToken);
        addSecureCookie(response, JwtUtil.REFRESH_TOKEN, refreshToken);

        // 클라이언트용 userId 쿠키 추가
        addClientCookie(response, "userId", user.getId().toString());

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken,
                new Date(System.currentTimeMillis() + JwtUtil.REFRESH_TOKEN_EXPIRE_TIME));
    }

    private void addSecureCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void addClientCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false);  // JavaScript에서 접근 가능
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}