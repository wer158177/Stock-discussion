package com.hangha.userservice.infrastructure.security.filter;


import com.hangha.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import com.hangha.userservice.infrastructure.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JwtAuthorizationFilter 호출됨: 요청 URI = {}", req.getRequestURI());
        String tokenValue = jwtUtil.getTokenFromRequest(req);

        // 토큰 값을 제대로 가져왔는지 확인
        if (StringUtils.hasText(tokenValue)) {
            log.info("요청에서 받은 토큰: {}", tokenValue);

            // JWT 토큰 substring
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info("검증할 액세스 토큰: {}", tokenValue);

            if (!jwtUtil.validateToken(tokenValue)) {
                log.warn("액세스 토큰이 만료되었습니다. 토큰: {}", tokenValue);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                return;
            } else {
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

                // Claims에서 정보를 제대로 가져오는지 확인
                if (info == null) {
                    log.error("토큰에서 사용자 정보를 가져올 수 없습니다.");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                    return;
                }

                log.info("토큰에서 사용자 정보: {}", info.getSubject());
                setAuthentication(info.getSubject());
            }
        } else {
            log.warn("토큰이 요청에 포함되어 있지 않습니다.");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
        }

        filterChain.doFilter(req, res); // 다음 필터로 이동
    }

    private void setAuthentication(String email) {
        // 빈 SecurityContext 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 인증 객체 생성
        Authentication authentication = createAuthentication(email);

        // 로그: 인증 객체가 제대로 생성되었는지 확인
        log.info("Authentication object created: {}", authentication);

        // SecurityContext에 인증 객체 설정
        context.setAuthentication(authentication);

        // 로그: SecurityContext에 인증 객체 설정 후
        log.info("SecurityContext set with authentication: {}", context.getAuthentication());

        // SecurityContextHolder에 설정된 인증 객체 저장
        SecurityContextHolder.setContext(context);

        // 로그: 최종적으로 SecurityContext가 저장되었는지 확인
        log.info("SecurityContextHolder current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
    }


    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email);
        }
        log.info(userDetails.getUsername()+" "+userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }



}
