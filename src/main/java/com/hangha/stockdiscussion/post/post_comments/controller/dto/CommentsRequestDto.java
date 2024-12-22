package com.hangha.stockdiscussion.post.post_comments.controller.dto;


import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsRequestDto {

    private Long postId;
    private Long parentId;
    private Long commentId;

    @NotNull
    @Size(min = 1, max = 1000)
    private String content;


    public CommentCommand writeCommand(Long userId) {
        return new CommentCommand(
                userId,
                this.postId,
                this.parentId,
                this.content  // 내용
        );
    }

    public CommentUpdateCommand updateCommand(Long userId){
        return new CommentUpdateCommand(
                userId,
                this.commentId,
                this.postId,
                this.parentId,
                this.content
        );
    }
}
