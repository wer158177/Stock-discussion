package com.hangha.stockdiscussion.post.post_comments.controller;


import com.hangha.stockdiscussion.post.post_comments.application.CommentsApplicationService;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.CommentsRequestDto;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.stockdiscussion.User.infrastructure.security.jwt.JwtUtil;

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


    @PostMapping("/write")
    public ResponseEntity<String> write(HttpServletRequest request, @RequestBody CommentsRequestDto commentsRequestDto) {
        Long userId = jwtUtil.extractUserIdFromToken(request);
        commentsApplicationService.commentWrite(userId, commentsRequestDto);
        return ResponseEntity.ok("댓글 작성완료");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<SimpleCommentResponseDto>> fetchComments(@PathVariable Long postId) {
        List<SimpleCommentResponseDto> comments = commentsApplicationService.commentRead(postId);
        return ResponseEntity.ok(comments);
    }


    @PutMapping("/{PostId}/{CommentId}")
    public ResponseEntity<String> update(HttpServletRequest request,
                                         @PathVariable Long PostId,
                                         @PathVariable Long CommentId,
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


}
