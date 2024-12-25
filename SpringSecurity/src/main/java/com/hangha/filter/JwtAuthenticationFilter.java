package com.hangha.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.dto.LoginRequestDto;
import com.hangha.dto.UserResponseDto;
import com.hangha.jwt.JwtUtil;
import com.hangha.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;
//    @Value("${user-service.base-url}")
//    private String userServiceBaseUrl;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");

        try {
            // 요청 데이터 파싱
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("파싱된 로그인 요청 데이터: {}", requestDto.getEmail()+" "+requestDto.getPassword());

            // 인증 API 호출
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequestDto> entity = new HttpEntity<>(requestDto, headers);
            String url = "http://localhost:8082/api/user/authenticate";
            ResponseEntity<UserResponseDto> userResponse = restTemplate.postForEntity(url, entity, UserResponseDto.class);

            log.info("인증 API 응답 상태: {}", userResponse.getStatusCode());

            // 응답 상태 확인
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                log.warn("유저 인증 실패: 응답 상태 코드 {}", userResponse.getStatusCode());
                throw new RuntimeException("유저 인증 실패: 상태 코드 " + userResponse.getStatusCode());
            }

            // 유저 정보를 UserDetails로 변환
            UserDetailsImpl userDetails = UserDetailsImpl.fromDto(userResponse.getBody());
            log.info("인증된 유저 정보: {}", userDetails.getUsername());

            // UsernamePasswordAuthenticationToken 생성 후 반환
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (IOException e) {
            log.error("로그인 요청 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("로그인 요청 파싱 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("유저 인증 실패: {}", e.getMessage());
            throw new RuntimeException("유저 인증 실패: " + e.getMessage(), e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getActualUsername();
        String role = ((UserDetailsImpl) authResult.getPrincipal()).getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUserId();

        System.out.println("jwt 생성 "+email);

        String accessToken = jwtUtil.createToken(userId,email, username , role);
        String refreshToken = jwtUtil.createRefreshToken(email, username, role);

        jwtUtil.addJwtToCookie(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

        log.info("액세스 토큰과 리프레시 토큰 발급 완료");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
