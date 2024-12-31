package com.hangha.userservice.infrastructure.security.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "LoggingFilter")
@Component
@Order(1)
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 전처리
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String url = httpServletRequest.getRequestURI();
            log.info("Request URL: {}", url);

            // 다음 필터로 요청 전달
            chain.doFilter(request, response);

            // 후처리
            log.info("Response Status: {}", ((HttpServletResponse) response).getStatus());
        } catch (Exception e) {
            log.error("LoggingFilter 처리 중 오류 발생: {}", e.getMessage());
            throw e; // 예외를 다시 던져 요청 처리가 중단되지 않도록 함
        }
    }
}
