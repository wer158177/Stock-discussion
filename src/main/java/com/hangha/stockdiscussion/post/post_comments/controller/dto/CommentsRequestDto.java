package com.hangha.stockdiscussion.post.post_comments.controller.dto;


import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsRequestDto {

    private Long postId;
    private Long commentId;

    @NotNull
    private String comment;


    public CommentCommand writeCommand(Long userId) {
        return new CommentCommand(
                userId,
                this.postId,
                this.comment  // 내용
        );
    }

    public CommentUpdateCommand updateCommand(Long userId){
        return new CommentUpdateCommand(
                userId,
                this.commentId,
                this.postId,
                this.comment
        );
    }
}
