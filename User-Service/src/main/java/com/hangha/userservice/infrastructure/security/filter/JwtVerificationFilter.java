package com.hangha.userservice.infrastructure.security.filter;

import com.hangha.common.jwt.JwtUtil;
import com.hangha.userservice.infrastructure.security.service.UserDetailsImpl;
import com.hangha.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtVerificationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("JwtVerificationFilter 실행 시작: " + request.getRequestURI());

        String tokenValue = jwtUtil.getTokenFromRequest(request.getHeader("Authorization"));
        if (StringUtils.hasText(tokenValue)) {
            try {
                String token = jwtUtil.substringToken(tokenValue);
                if (!jwtUtil.validateToken(token)) {
                    log.warn("유효하지 않은 JWT 토큰: {}", token);
                    throw new IllegalArgumentException("Invalid JWT Token");
                }

                Claims claims = jwtUtil.getUserInfoFromToken(token);
                String email = claims.getSubject();

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증 성공, 사용자 이메일: {}", email);
            } catch (Exception e) {
                log.error("JWT 인증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("JWT 토큰이 요청에 포함되지 않음");
        }

        filterChain.doFilter(request, response);
    }
}