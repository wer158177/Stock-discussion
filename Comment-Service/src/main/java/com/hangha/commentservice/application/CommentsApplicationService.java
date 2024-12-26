package com.hangha.commentservice.application;

import com.hangha.stockdiscussion.post.domain.service.PostStatusService;
import com.hangha.stockdiscussion.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.stockdiscussion.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post_comments.controller.dto.CommentsRequestDto;
import com.hangha.stockdiscussion.post_comments.domain.service.CommentLikesService;
import com.hangha.stockdiscussion.post_comments.domain.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsApplicationService {
    private final CommentService commentService;
    private final PostStatusService postStatusService;
    private final CommentLikesService commentLikesService;
    public CommentsApplicationService(CommentService commentService, PostStatusService postStatusService, CommentLikesService commentLikesService) {
        this.commentService = commentService;
        this.postStatusService = postStatusService;
        this.commentLikesService = commentLikesService;
    }

    @Transactional
    public void commentWrite(Long userId, CommentsRequestDto commentsRequestDto){
        CommentCommand command = commentsRequestDto.writeCommand(userId);
        commentService.writeComment(userId,command);
        postStatusService.updateCommentCount(command.postId(), true);

    }
    public void commentUpdate(Long userId, CommentsRequestDto commentsRequestDto){
        CommentUpdateCommand command = commentsRequestDto.updateCommand(userId);
        commentService.updateComment(userId,command);

    }

    @Transactional
    public void commentDelete(Long userId,Long commentId,Long postId){
        commentService.deleteComment(userId,commentId,postId);
        postStatusService.updateCommentCount(postId, false);
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



