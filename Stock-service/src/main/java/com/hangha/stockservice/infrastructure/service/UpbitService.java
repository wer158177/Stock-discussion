package com.hangha.stockservice.infrastructure.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Slf4j
@Service
public class UpbitService {

    private final RestTemplate restTemplate;

    @Value("${upbit.access-key}")
    private String accessKey;

    @Value("${upbit.secret-key}")
    private String secretKey;

    public UpbitService() {
        this.restTemplate = new RestTemplate();
    }

    public String getCandleData(String interval, String market, String to, int count) {
        try {
            // 요청 URL 생성
            String url = String.format(
                    "https://api.upbit.com/v1/candles/%s?market=%s&count=%d&to=%s",
                    interval, market, count, to
            );

            // Query String 생성
            String queryString = String.format("market=%s&count=%d&to=%s", market, count, to);
            System.out.printf("Query String: %s%n", queryString);

            // Query String 해싱
            String queryHash = hashQueryString(queryString);
            System.out.printf("Query Hash: %s%n", queryHash);

            // JWT 생성
            String jwtToken = createJwtToken(queryHash);
            System.out.printf("Generated JWT: %s%n", jwtToken);
            logDecodedJwt(jwtToken);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);
            headers.add("Accept", "application/json");

            // 요청 정보 로그
            System.out.printf("Request URL: %s%n", url);
            System.out.printf("Request Headers: %s%n", headers);

            // HTTP 요청
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // 응답 정보 로그
            System.out.printf("Response Status Code: %s%n", response.getStatusCode());
            System.out.printf("Response Body: %s%n", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            System.err.printf("Error calling Upbit API: %s%n", e.getMessage());
            throw new RuntimeException("Failed to fetch candle data from Upbit API", e);
        }
    }


    private void logDecodedJwt(String jwtToken) {
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        System.out.printf("Decoded JWT Payload: %s%n", decodedJWT.getPayload());
    }


    public String getMinuteCandles(String market, String unit, String to, int count) {
        try {
            StringBuilder urlBuilder = new StringBuilder()
                    .append("https://api.upbit.com/v1/candles/minutes/")
                    .append(unit)
                    .append("?market=")
                    .append(market)
                    .append("&count=")
                    .append(count);

            StringBuilder queryStringBuilder = new StringBuilder()
                    .append("market=")
                    .append(market)
                    .append("&count=")
                    .append(count);

            // to 파라미터가 있는 경우에만 추가
            if (to != null && !to.isEmpty()) {
                urlBuilder.append("&to=").append(to);
                queryStringBuilder.append("&to=").append(to);
            }

            String url = urlBuilder.toString();
            String queryString = queryStringBuilder.toString();

            log.debug("Request URL: {}", url);
            log.debug("Query String: {}", queryString);

            String queryHash = hashQueryString(queryString);
            String jwtToken = createJwtToken(queryHash);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);
            headers.add("Accept", "application/json");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            log.debug("Response Status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("분봉 데이터 조회 실패: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch minute candle data from Upbit API", e);
        }
    }


    private String createJwtToken(String queryHash) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("JWT 토큰 생성 실패", e);
        }
    }

    private String hashQueryString(String queryString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(queryString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Query string 해싱 실패", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}