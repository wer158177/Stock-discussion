package com.hangha.stockdiscussion.User.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.stockdiscussion.User.domain.entity.UserRoleEnum;
import com.hangha.stockdiscussion.User.infrastructure.security.dto.LoginRequestDto;
import com.hangha.stockdiscussion.User.infrastructure.security.jwt.JwtUtil;
import com.hangha.stockdiscussion.User.infrastructure.security.service.RefreshTokenService;
import com.hangha.stockdiscussion.User.infrastructure.security.service.UserDetailsImpl;
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

@Slf4j(topic = "로그인 및 JWT 생성")
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
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getEmail(); // 이메일 사용
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUsername(); // 사용자 이름
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserRole();
        Long userId =((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();// 사용자 ID

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createToken(userId,email, username, role); // 이메일과 사용자 이름을 사용하여 액세스 토큰 생성

        // 리프레시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(email, username,role); // 이메일과 사용자 이름을 사용하여 리프레시 토큰 생성

        // 쿠키에 추가
        jwtUtil.addJwtToCookie(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

        log.info(accessToken);
        log.info(refreshToken);


        Date tokenExpiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME); // 리프레시 토큰 만료 시간 설정
        refreshTokenService.saveRefreshToken(userId, refreshToken, tokenExpiration); // 리프레시 토큰 저장

        log.info("액세스 토큰과 리프레시 토큰 발급 완료");
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}