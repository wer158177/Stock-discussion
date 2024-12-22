package com.hangha.stockdiscussion.post.controller;


import com.hangha.stockdiscussion.post.application.PostApplicationService;
import com.hangha.stockdiscussion.post.controller.dto.PostRequestDto;
import com.hangha.stockdiscussion.User.infrastructure.security.jwt.JwtUtil;
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
        Long userId = jwtUtil.extractUserIdFromToken(request);
        postApplicationService.postWrite(userId, postRequestDto);
        return ResponseEntity.ok("작성완료");
    }


    @PutMapping("/{PostId}")
    public ResponseEntity<String> update(HttpServletRequest request,
                                         @PathVariable Long PostId,
                                         @RequestBody PostRequestDto postRequestDto) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        postApplicationService.postUpdate(userId, postRequestDto);
        return ResponseEntity.ok("업데이트 완료");
    }


    @DeleteMapping("/{PostId}")
    public ResponseEntity<String> delete(HttpServletRequest request, @PathVariable Long PostId) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        postApplicationService.postDelete(userId,PostId);
        return ResponseEntity.ok("삭제완료");
    }


}
