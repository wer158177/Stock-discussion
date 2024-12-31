package com.hangha.userservice.infrastructure.security.config;

import com.hangha.common.JwtUtil;
import com.hangha.userservice.infrastructure.security.filter.ExceptionHandlerFilter;
import com.hangha.userservice.infrastructure.security.filter.JwtAuthenticationFilter;
import com.hangha.userservice.infrastructure.security.filter.JwtVerificationFilter;
import com.hangha.userservice.infrastructure.security.service.RefreshTokenService;
import com.hangha.userservice.infrastructure.security.service.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${allowed.origin:http://localhost:8000}")
    private String allowedOrigin;

    public WebSecurityConfig(JwtUtil jwtUtil,
                             AuthenticationConfiguration authenticationConfiguration,
                             RefreshTokenService refreshTokenService,
                             UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
        log.info("WebSecurityConfig 생성 완료 - JwtUtil: {}, UserDetailsServiceImpl: {}", jwtUtil, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("AuthenticationManager 빈 생성");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        log.info("JwtAuthenticationFilter 빈 생성 시작");
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, refreshTokenService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        log.info("JwtAuthenticationFilter 빈 생성 완료");
        return filter;
    }

    @Bean
    public JwtVerificationFilter jwtVerificationFilter() {
        log.info("JwtVerificationFilter 빈 생성 시작");
        JwtVerificationFilter filter = new JwtVerificationFilter(jwtUtil, userDetailsService);
        log.info("JwtVerificationFilter 빈 생성 완료");
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain 설정 시작");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**", "/api/user/refresh").permitAll()
                        .requestMatchers("/api/following/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.addAllowedOrigin(allowedOrigin);
                    configuration.addAllowedMethod("*");
                    configuration.addAllowedHeader("*");
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtVerificationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new ExceptionHandlerFilter(), JwtVerificationFilter.class);

        log.info("SecurityFilterChain 설정 완료");

        return http.build();
    }
}
