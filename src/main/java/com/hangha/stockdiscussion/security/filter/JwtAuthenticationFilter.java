package com.hangha.stockdiscussion.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.stockdiscussion.User.domain.entity.UserRoleEnum;
import com.hangha.stockdiscussion.security.dto.LoginRequestDto;
import com.hangha.stockdiscussion.security.jwt.JwtUtil;
import com.hangha.stockdiscussion.security.service.UserDetailsImpl;
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

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUsername();  // 수정됨
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserRole();

        String token = jwtUtil.createToken(username, role);
        log.info(token);
        jwtUtil.addJwtToCookie(token, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}