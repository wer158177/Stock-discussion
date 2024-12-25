package com.hangha.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization"; // 액세스 토큰 쿠키 이름
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";   // 리프레시 토큰 쿠키 이름// 권한 키
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_KEY = "role"; // 사용자 권한 값의 KEY

    // 토큰 Prefix
    private final long TOKEN_TIME = 60 * 60 * 1000L;                    // 액세스 토큰 1시간
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;  // 리프레시 토큰 7일

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 액세스 토큰 생성
     */
    public String createToken(Long userId, String email, String username, String role) {
        Date now = new Date();
        System.out.println(secretKey);
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("userId", userId)
                        .claim("username", username)
                        .claim(AUTHORIZATION_KEY,role)
                        .setExpiration(new Date(now.getTime() + TOKEN_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();

    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String email, String username, String role) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("username", username)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    /**
     * JWT 쿠키 추가
     */
    public void addJwtToCookie(String token, HttpServletResponse response) {
        addCookie(AUTHORIZATION_HEADER, token, response, 60 * 60 * 24);
    }

    /**
     * 리프레시 토큰 쿠키 추가
     */
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        addCookie(REFRESH_TOKEN_HEADER, refreshToken, response, (int) (REFRESH_TOKEN_TIME / 1000));
    }

    /**
     * 공통 쿠키 추가 메서드
     */
    private void addCookie(String name, String value, HttpServletResponse response, int maxAge) {
        try {
            value = URLEncoder.encode(value, "utf-8").replaceAll("\\+", "%20");
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);  // HTTPS 사용 시 설정
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error("쿠키 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("쿠키 생성 오류");
        }
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    /**
     * 리프레시 토큰 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("유효하지 않은 JWT 서명입니다. 메시지: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰입니다. 만료일: {}", e.getClaims().getExpiration());
        } catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT 토큰입니다. 메시지: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 JWT 토큰입니다. 메시지: {}", e.getMessage());
        }
        return false;
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 요청에서 JWT 토큰 추출
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        return getCookieValue(request, AUTHORIZATION_HEADER);
    }

    /**
     * 요청에서 리프레시 토큰 추출
     */
    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        String token = getCookieValue(request, REFRESH_TOKEN_HEADER);
        logger.info("리프레시 토큰: {}", token);  // 토큰 로그 추가
        return token;
    }

    /**
     * 쿠키에서 값 추출
     */
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("쿠키 디코딩 오류: {}", e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // "Bearer " 제거
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }


    // 유저 ID를 추출하는 메서드
    public Long extractUserIdFromToken(HttpServletRequest request) {
        String tokenValue = getTokenFromRequest(request);
        if (tokenValue != null) {
            // "Bearer " 부분 제거
            String token = substringToken(tokenValue);

            // 토큰 검증
            if (validateToken(token)) {
                // 토큰에서 정보 추출
                Claims claims = getUserInfoFromToken(token);
                return claims.get("userId", Long.class);
            }
        }
        return null;

    }
}
