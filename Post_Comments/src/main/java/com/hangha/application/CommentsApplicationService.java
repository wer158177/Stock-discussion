package com.hangha.application;

import com.hangha.application.command.CommentCommand;
import com.hangha.application.command.CommentUpdateCommand;
import com.hangha.controller.dto.CommentsRequestDto;
import com.hangha.controller.dto.SimpleCommentResponseDto;
import com.hangha.domain.entity.PostComments;
import com.hangha.domain.service.CommentLikesService;
import com.hangha.domain.service.CommentService;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsApplicationService {
    private final CommentService commentService;

    private final CommentLikesService commentLikesService;
    public CommentsApplicationService(CommentService commentService, CommentLikesService commentLikesService) {
        this.commentService = commentService;
        this.commentLikesService = commentLikesService;
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



    //댓글조회
    public List<SimpleCommentResponseDto> getParentComments(Long postId) {
        // 도메인 객체를 DTO로 변환
        return commentService.findParentCommentsByPostId(postId)
                .stream()
                .map(this::toSimpleCommentResponseDto)
                .toList();
    }
    //대댓글조회
    public List<SimpleCommentResponseDto> getReplies(Long parentId) {
        // 도메인 객체를 DTO로 변환
        return commentService.findRepliesByParentId(parentId)
                .stream()
                .map(this::toSimpleCommentResponseDto)
                .toList();
    }

    //댓글조회 dto 반환메소드
    private SimpleCommentResponseDto toSimpleCommentResponseDto(PostComments comment) {
        return new SimpleCommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUserId(),
                comment.getCreatedAt()
        );
    }

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        commentLikesService.likeComment(commentId, userId);

    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        commentLikesService.unlikeComment(commentId, userId);
    }

}



