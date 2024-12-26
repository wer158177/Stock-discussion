package com.hangha.postservice.controller;


import com.hangha.postservice.application.PostApplicationService;
import com.hangha.postservice.controller.dto.PostRequestDto;
import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.service.PostStatusService;

import com.hangha.userservice.infrastructure.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final JwtUtil jwtUtil;
    private final PostApplicationService postApplicationService;
    private final PostStatusService postStatusService;
    public PostController(JwtUtil jwtUtil, PostApplicationService postApplicationService, PostStatusService postStatusService) {
        this.jwtUtil = jwtUtil;
        this.postApplicationService = postApplicationService;
        this.postStatusService = postStatusService;
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



    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(HttpServletRequest request, @RequestParam Long postId) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        postApplicationService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(HttpServletRequest request, @RequestParam Long postId) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        postApplicationService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    // 조회수 증가
    @PostMapping("/{postId}/view")
    public ResponseEntity<Void> increaseViewCount(@PathVariable Long postId) {
        postStatusService.increaseViewCount(postId);
        return ResponseEntity.ok().build();
    }


    // 특정 게시글의 상태 조회
    @GetMapping("/{postId}/status")
    public ResponseEntity<PostStatusResponse> getPostStatus(@PathVariable Long postId) {
        PostStatusResponse postStatus = postApplicationService.getPostStatus(postId);
        return ResponseEntity.ok(postStatus);
    }

}
