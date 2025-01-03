package com.hangha.postservice.controller;


import com.hangha.common.JwtUtil;
import com.hangha.postservice.application.PostApplicationService;
import com.hangha.postservice.controller.dto.PostRequestDto;
import com.hangha.postservice.controller.dto.PostResponseDto;
import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.service.PostService;
import com.hangha.postservice.domain.service.PostStatusService;


import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final JwtUtil jwtUtil;
    private final PostApplicationService postApplicationService;
    private final PostStatusService postStatusService;
    private final PostService postService;

    public PostController(JwtUtil jwtUtil, PostApplicationService postApplicationService, PostStatusService postStatusService, PostStatusService postStatusService1, PostService postService) {
        this.jwtUtil = jwtUtil;
        this.postApplicationService = postApplicationService;
        this.postStatusService = postStatusService1;
        this.postService = postService;
    }


    @PostMapping("/write")
    public ResponseEntity<String> write(
            @RequestHeader("X-Claim-userId") Long userId,
            @RequestBody PostRequestDto postRequestDto) {
        postApplicationService.postWrite(userId, postRequestDto);
        return ResponseEntity.ok("작성완료");
    }


    @PutMapping("/{PostId}")
    public ResponseEntity<String> update(@RequestHeader("X-Claim-userId") Long userId,
                                         @PathVariable Long PostId,
                                         @RequestBody PostRequestDto postRequestDto) {
        postApplicationService.postUpdate(userId, postRequestDto);
        return ResponseEntity.ok("업데이트 완료");
    }


    @DeleteMapping("/{PostId}")
    public ResponseEntity<String> delete( @RequestHeader("X-Claim-userId") Long userId, @PathVariable Long PostId) {
        postApplicationService.postDelete(userId,PostId);
        return ResponseEntity.ok("삭제완료");
    }



    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost( @RequestHeader("X-Claim-userId") Long userId, @RequestParam Long postId) {
        postApplicationService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost( @RequestHeader("X-Claim-userId") Long userId, @RequestParam Long postId) {
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


    // 게시글 존재 여부 확인 API
    @GetMapping("/{postId}/exists")
    public boolean doesPostExist(@PathVariable Long postId) {
        return postService.existsById(postId);
    }

    // 댓글 수 업데이트 API
    @PostMapping("/{postId}/comments/count")
    public void updateCommentCount(@PathVariable Long postId, @RequestParam boolean increment) {
        postStatusService.updateCommentCount(postId, increment);
    }



    //필요없어질수도있음
    @GetMapping()
    public ResponseEntity<List<PostResponseDto>> getPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        List<PostResponseDto> posts = postService.getPosts(page, size);
        return ResponseEntity.ok(posts);
    }


    // 랜덤 게시글 조회 API
    @GetMapping("/random")
    public List<PostResponseDto> getRandomPosts(@RequestParam(defaultValue = "10") int size) {
        return postService.getRandomPosts(size);
    }


}
