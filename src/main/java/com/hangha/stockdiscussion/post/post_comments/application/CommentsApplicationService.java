package com.hangha.stockdiscussion.post.post_comments.application;

import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.domain.service.CommentInterface;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.CommentsRequestDto;
import org.springframework.stereotype.Service;

@Service
public class CommentsApplicationService {
    private final CommentInterface commentInterface;

    public CommentsApplicationService(CommentInterface commentInterface) {
        this.commentInterface = commentInterface;
    }


    public void commentWrite(Long userId, CommentsRequestDto commentsRequestDto){
        CommentCommand command = commentsRequestDto.writeCommand(userId);
        commentInterface.writeComment(userId,command);

    }
    public void commentUpdate(Long userId, CommentsRequestDto commentsRequestDto){
        CommentUpdateCommand command = commentsRequestDto.updateCommand(userId);
        commentInterface.updateComment(userId,command);

    }
    public void commentDelete(Long userId,Long commentId,Long postId){
        commentInterface.deleteComment(userId,commentId,postId);

    }
}
