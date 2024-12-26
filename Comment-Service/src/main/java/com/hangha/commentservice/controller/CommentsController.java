package com.hangha.commentservice.controller;


import com.hangha.stockdiscussion.post_comments.application.CommentsApplicationService;
import com.hangha.stockdiscussion.post_comments.controller.dto.CommentsRequestDto;
import com.hangha.stockdiscussion.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.userservice.infrastructure.security.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post_comments")
public class CommentsController {

    private final JwtUtil jwtUtil;
    private final CommentsApplicationService commentsApplicationService;

    public CommentsController(JwtUtil jwtUtil, CommentsApplicationService commentsApplicationService) {
        this.jwtUtil = jwtUtil;
        this.commentsApplicationService = commentsApplicationService;
    }


    @PostMapping("/{postId}")
    public ResponseEntity<String> write(
                                         HttpServletRequest request,
                                         @PathVariable Long postId,
                                         @RequestBody CommentsRequestDto commentsRequestDto) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        commentsApplicationService.commentWrite(userId,commentsRequestDto);
        return ResponseEntity.ok("댓글 작성완료");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<SimpleCommentResponseDto>> getParentComments(@PathVariable Long postId) {
        List<SimpleCommentResponseDto> parentComments = commentsApplicationService.getParentComments(postId);
        return ResponseEntity.ok(parentComments);
    }


    @GetMapping("/{postId}/comments/{parentId}/replies")
    public ResponseEntity<List<SimpleCommentResponseDto>> getReplies(@PathVariable Long parentId) {
        List<SimpleCommentResponseDto> replies = commentsApplicationService.getReplies(parentId);
        return ResponseEntity.ok(replies);
    }


    @PutMapping("/{PostId}/comments/{commentId}")
    public ResponseEntity<String> update(HttpServletRequest request,
                                         @PathVariable Long PostId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentsRequestDto commentsRequestDto) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        commentsApplicationService.commentUpdate(userId, commentsRequestDto);
        return ResponseEntity.ok("댓글 업데이트 완료");
    }

    @DeleteMapping("/{postId}/{commentId}")
    public ResponseEntity<String>  delete(HttpServletRequest request,
                                          @PathVariable Long postId,
                                          @PathVariable Long commentId) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        commentsApplicationService.commentDelete(userId,postId,commentId);
        return ResponseEntity.ok("댓글 삭제완료");
    }



    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentsApplicationService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentsApplicationService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }




}
