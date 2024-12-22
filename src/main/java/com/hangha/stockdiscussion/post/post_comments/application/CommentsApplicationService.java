package com.hangha.stockdiscussion.post.post_comments.application;

import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.CommentsRequestDto;
import com.hangha.stockdiscussion.post.post_comments.domain.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsApplicationService {
    private final CommentService commentService;

    public CommentsApplicationService(CommentService commentService) {
        this.commentService = commentService;
    }


    public void commentWrite(Long userId, CommentsRequestDto commentsRequestDto){
        CommentCommand command = commentsRequestDto.writeCommand(userId);
        commentService.writeComment(userId,command);

    }
    public void commentUpdate(Long userId, CommentsRequestDto commentsRequestDto){
        CommentUpdateCommand command = commentsRequestDto.updateCommand(userId);
        commentService.updateComment(userId,command);

    }
    public void commentDelete(Long userId,Long commentId,Long postId){
        commentService.deleteComment(userId,commentId,postId);

    }






    public List<SimpleCommentResponseDto> getParentComments(Long postId) {
        // 부모 댓글 조회 및 DTO 변환
        List<SimpleCommentResponseDto> parentComments = commentService.findParentCommentsByPostId(postId);
        return parentComments.stream()
                .map(comment -> new SimpleCommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUserId(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    public List<SimpleCommentResponseDto> getReplies(Long parentId) {
        // 대댓글 조회 및 DTO 변환
        List<SimpleCommentResponseDto> replies = commentService.findRepliesByParentId(parentId);
        return replies.stream()
                .map(reply -> new SimpleCommentResponseDto(
                        reply.getId(),
                        reply.getContent(),
                        reply.getUserId(),
                        reply.getCreatedAt()
                ))
                .toList();
    }

}



