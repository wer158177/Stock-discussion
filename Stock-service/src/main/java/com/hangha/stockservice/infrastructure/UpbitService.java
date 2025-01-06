package com.hangha.stockservice.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

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

    private String createJwtToken(String queryHash) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString()) // UUID로 nonce 생성
                    .withClaim("query_hash", queryHash) // Query 해시 추가
                    .withClaim("query_hash_alg", "SHA512") // 알고리즘 정보 추가
                    .sign(algorithm); // 서명
        } catch (Exception e) {
            System.err.printf("Error generating JWT: %s%n", e.getMessage());
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    private String hashQueryString(String queryString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(queryString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            System.err.printf("Error hashing query string: %s%n", e.getMessage());
            throw new RuntimeException("Failed to hash query string", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }


    private void logDecodedJwt(String jwtToken) {
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        System.out.printf("Decoded JWT Payload: %s%n", decodedJWT.getPayload());
    }
}
