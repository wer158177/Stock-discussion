package com.hangha.stockdiscussion.post.controller;


import com.hangha.stockdiscussion.post.application.PostApplicationService;
import com.hangha.stockdiscussion.post.controller.dto.PostRequestDto;
import com.hangha.stockdiscussion.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final JwtUtil jwtUtil;
    private final PostApplicationService postApplicationService;

    public PostController(JwtUtil jwtUtil, PostApplicationService postApplicationService) {
        this.jwtUtil = jwtUtil;
        this.postApplicationService = postApplicationService;
    }


    @PostMapping("/write")
    public ResponseEntity<String> write(HttpServletRequest request, @RequestBody PostRequestDto postRequestDto) {
        Long userId = extractUserIdFromToken(request);
        postApplicationService.postWrite(userId, postRequestDto);
        return ResponseEntity.ok("작성완료");
    }


    @PutMapping("/{PostId}")
    public ResponseEntity<String> update(HttpServletRequest request,
                                         @PathVariable Long PostId,
                                         @RequestBody PostRequestDto postRequestDto) {
        Long userId = extractUserIdFromToken(request);
        postApplicationService.postUpdate(userId, postRequestDto);
        return ResponseEntity.ok("업데이트 완료");
    }


    public Long extractUserIdFromToken(HttpServletRequest request) {
        String tokenValue = jwtUtil.getTokenFromRequest(request);
        if (tokenValue != null) {
            // "Bearer " 부분 제거
            String token = jwtUtil.substringToken(tokenValue);

            // 토큰 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 정보 추출
                Claims claims = jwtUtil.getUserInfoFromToken(token);
                return claims.get("userId", Long.class);
            }
        }
        return null;
    }

}
