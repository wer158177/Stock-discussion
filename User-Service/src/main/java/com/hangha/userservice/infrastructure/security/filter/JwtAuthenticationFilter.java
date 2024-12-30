package com.hangha.userservice.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hangha.common.JwtUtil;
import com.hangha.userservice.domain.entity.UserRoleEnum;
import com.hangha.userservice.infrastructure.security.dto.LoginRequestDto;

import com.hangha.userservice.infrastructure.security.dto.TokenResponse;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // 요청에서 로그인 DTO 읽기
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            // 이메일과 패스워드로 인증 객체 생성
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),  // 수정된 부분
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error("로그인 요청 DTO 매핑 실패: {}", e.getMessage());
            throw new RuntimeException("로그인 요청이 잘못되었습니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        // 사용자 정보 가져오기
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getEmail();
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUsername();
        String role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserRole().name();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createToken(userId, email, username, role);

        // 리프레시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(email, username, role);


        log.info("엑세스토큰{}", accessToken);
        log.info("리프레시토큰{}", refreshToken);
        log.info("액세스 토큰과 리프레시 토큰 쿠키 설정 완료");

        // 쿠키에 토큰 설정 (엑세스 토큰과 리프레시 토큰)
        jwtUtil.addCookie("ACCESS_TOKEN", accessToken, response, 3600);
        jwtUtil.addCookie("REFRESH_TOKEN", refreshToken, response, (int) REFRESH_TOKEN_TIME );

        // 리프레시 토큰 저장 (DB 등)
        Date tokenExpiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME);
        refreshTokenService.saveRefreshToken(userId, refreshToken, tokenExpiration);

        // 추가적으로 JSON 응답을 보낼 수도 있음 (선택 사항)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), Map.of("message", "로그인 성공"));
    }




    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}