package com.hangha.gatewayservice.Filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component("JwtFilter")
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    @Value("${jwt.secret.key}")
    private String jwtSecret;  // JWT 서명 검증을 위한 비밀 키

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/user/register", // 회원가입
            "/api/user/login",    // 로그인
            "/api/user/refresh"   // 토큰 갱신
    );


    public JwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 요청 경로 확인
            String path = request.getURI().getPath();

            // 인증이 필요 없는 경로인지 확인
            if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
                log.info("Skipping JWT filter for path: {}", path);
                return chain.filter(exchange); // 필터 우회
            }


            // Authorization 헤더가 없으면 필터를 계속 진행
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("Authorization header not found.");
                return chain.filter(exchange);
            }

            // Authorization 헤더에서 Bearer 토큰 추출
            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
            try {
                // URL 디코딩
                authHeader = URLDecoder.decode(authHeader, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                log.warn("Failed to decode Authorization header: {}", e.getMessage());
                return handleUnauthorized(response, "Invalid Authorization header format.");
            }

            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format. Expected 'Bearer <token>'");
                return handleUnauthorized(response, "Invalid Authorization header format.");
            }

            // Bearer 뒤의 실제 토큰 추출
            String token = authHeader.substring(7);
            log.info("Extracted token: {}", token);

            try {
                // JWT 토큰을 검증하고, 클레임을 추출
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSecret)  // JWT 서명 검증
                        .parseClaimsJws(token)     // JWT 파싱
                        .getBody();                // 클레임 추출

                log.info("JWT Claims extracted: ");
                // 클레임을 로그로 출력
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    log.info("{}: {}", entry.getKey(), entry.getValue());
                }

                ServerHttpRequest.Builder mutatedRequest = request.mutate();
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    mutatedRequest.header("X-Claim-" + entry.getKey(), String.valueOf(entry.getValue()));
                }

                request = mutatedRequest.build();
                exchange = exchange.mutate().request(request).build();

                // 엑세스 토큰을 쿠키로 클라이언트에게 반환
                log.info("Setting ACCESS_TOKEN cookie.");
                response.getHeaders().add(HttpHeaders.SET_COOKIE, "ACCESS_TOKEN=" + token + "; Path=/; HttpOnly; Secure");

                // 유저 서비스에서 받은 리프레시 토큰을 그대로 쿠키로 반환
                String refreshToken = token;  // 예시로 엑세스 토큰을 리프레시 토큰으로 사용
                log.info("Setting REFRESH_TOKEN cookie.");
                response.getHeaders().add(HttpHeaders.SET_COOKIE, "REFRESH_TOKEN=" + refreshToken + "; Path=/; HttpOnly; Secure");

            } catch (Exception e) {
                log.error("JWT validation failed", e);  // 토큰 검증 실패 시 로그
                return handleUnauthorized(response, "JWT validation failed: " + e.getMessage());  // 에러 응답
            }

            log.info("JwtFilter completed successfully.");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter: response status code -> {}", response.getStatusCode());
            }));
        };
    }

    // Unauthorized 에러 처리
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        log.warn("Unauthorized access attempt: {}", message);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);  // 401 Unauthorized 상태 코드
        response.getHeaders().add("Content-Type", "application/json");  // 응답 타입 설정
        String body = String.format("{\"error\": \"%s\", \"message\": \"%s\"}", HttpStatus.UNAUTHORIZED.getReasonPhrase(), message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));  // 응답 본문 작성
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}
