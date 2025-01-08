package com.hangha.gatewayservice.Filter;

import com.hangha.common.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        super(Config.class);  // 상위 클래스 생성자 호출
        this.jwtUtil = jwtUtil;
    }

    @Data
    @NoArgsConstructor
    @Validated
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();

            if (config.isPreLogger()) {
                System.out.println("Pre Filter: Request path: " + path);
            }

            // 공개 경로인지 확인
            if (!isPublicPath(path)) {
                System.out.println("Private path detected: " + path);

                String accessToken = extractToken(request, JwtUtil.ACCESS_TOKEN);
                String refreshToken = extractToken(request, JwtUtil.REFRESH_TOKEN);

                System.out.println("Extracted Access Token: " + accessToken);
                System.out.println("Extracted Refresh Token: " + refreshToken);

                if (!StringUtils.hasText(accessToken)) {
                    if (StringUtils.hasText(refreshToken)) {
                        try {
                            Claims refreshClaims = jwtUtil.validateAndGetClaims(refreshToken);
                            System.out.println("Valid Refresh Token: " + refreshClaims);

                            String newAccessToken = jwtUtil.createToken(
                                    refreshClaims.get("userId", Long.class),
                                    refreshClaims.getSubject(),
                                    refreshClaims.get("username", String.class),
                                    refreshClaims.get("role", String.class),
                                    true
                            );

                            System.out.println("Generated New Access Token: " + newAccessToken);
                            addTokenCookie(exchange, JwtUtil.ACCESS_TOKEN, newAccessToken);
                            accessToken = newAccessToken;
                        } catch (Exception e) {
                            System.err.println("Invalid Refresh Token: " + e.getMessage());
                            return onError(exchange, "Invalid refresh token", HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        System.out.println("No tokens present");
                        return onError(exchange, "No tokens present", HttpStatus.UNAUTHORIZED);
                    }
                }

                try {
                    Claims claims = jwtUtil.validateAndGetClaims(accessToken);
                    System.out.println("Valid Access Token Claims: " + claims);
                    addAuthorizationHeaders(exchange, claims);
                } catch (Exception e) {
                    System.err.println("Invalid Access Token: " + e.getMessage());
                    return onError(exchange, "Invalid access token", HttpStatus.UNAUTHORIZED);
                }
            } else {
                System.out.println("Public path accessed: " + path);
            }

            return chain.filter(exchange);
        };
    }

    private String extractToken(ServerHttpRequest request, String cookieName) {
        HttpCookie cookie = request.getCookies().getFirst(cookieName);
        String token = cookie != null ? cookie.getValue() : null;
        System.out.println("Extracting Token for Cookie Name: " + cookieName + " -> " + token);
        return token;
    }

    private void addTokenCookie(ServerWebExchange exchange, String name, String token) {
        System.out.println("Adding Token to Cookie: " + name);
        exchange.getResponse().getHeaders().add(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from(name, token)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .build()
                        .toString()
        );
    }

    private void addAuthorizationHeaders(ServerWebExchange exchange, Claims claims) {
        System.out.println("Adding Authorization Headers: " + claims);
        exchange.getRequest().mutate()
                .header("X-Claim-userId", claims.get("userId").toString())
                .header("X-USER-EMAIL", claims.getSubject())
                .header("X-USER-ROLE", claims.get("role").toString())
                .build();
    }

    private boolean isPublicPath(String path) {
        // 공개 경로 목록
        List<String> publicPaths = Arrays.asList(
                "/api/auth/login",
                "/api/user/register",
                "/api/user/refresh",
                "/stock-service/ws/ticker",
                "/stock-service/ws/minute"
        );

        boolean isPublic = publicPaths.stream().anyMatch(path::contains);
        System.out.println("Checking if path is public: " + path + " -> " + isPublic);

        return isPublic;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        System.err.println("Error: " + message + ", Status: " + status);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}
