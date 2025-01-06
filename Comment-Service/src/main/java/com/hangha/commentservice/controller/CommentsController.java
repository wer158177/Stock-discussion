package com.hangha.commentservice.controller;



import com.hangha.commentservice.application.CommentsApplicationService;
import com.hangha.commentservice.controller.dto.CommentsRequestDto;
import com.hangha.commentservice.controller.dto.SimpleCommentResponseDto;
import com.hangha.commentservice.domain.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post_comments")
public class CommentsController {


    private final CommentsApplicationService commentsApplicationService;
    private final CommentService commentService;
    public CommentsController(CommentsApplicationService commentsApplicationService, CommentService commentService) {

        this.commentsApplicationService = commentsApplicationService;
        this.commentService = commentService;
    }


    @PostMapping("/{postId}")
    public ResponseEntity<String> write(
                                         @RequestHeader("X-Claim-userId")Long userId,
                                         @PathVariable Long postId,
                                         @RequestBody CommentsRequestDto commentsRequestDto) {
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
    public ResponseEntity<String> update(@RequestHeader("X-Claim-userId")Long userId,
                                         @PathVariable Long PostId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentsRequestDto commentsRequestDto) {
        commentsApplicationService.commentUpdate(userId, commentsRequestDto);
        System.out.println(commentsRequestDto.getContent());
        return ResponseEntity.ok("댓글 업데이트 완료");
    }

    @DeleteMapping("/{postId}/{commentId}")
    public ResponseEntity<String>  delete(@RequestHeader("X-Claim-userId")Long userId,
                                          @PathVariable Long postId,
                                          @PathVariable Long commentId) {
        commentsApplicationService.commentDelete(userId,postId,commentId);
        return ResponseEntity.ok("댓글 삭제완료");
    }



    @PostMapping("/{postId}/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId,
                                            @PathVariable Long postId,
                                            @RequestHeader("X-Claim-userId")Long userId) {
        commentsApplicationService.likeComment(commentId, userId,postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId,
                                              @PathVariable Long postId,
                                              @RequestHeader("X-Claim-userId")Long userId) {
        commentsApplicationService.unlikeComment(commentId, userId,postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}/exists")
    public boolean checkPostExists(@PathVariable Long postId) {
        return commentService.isPostAvailable(postId);
    }


}
