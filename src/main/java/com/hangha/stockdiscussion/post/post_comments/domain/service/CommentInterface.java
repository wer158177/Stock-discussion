package com.hangha.stockdiscussion.post.post_comments.domain.service;

import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;

import java.util.List;

public interface CommentInterface {
    //댓글 생성
    void writeComment(Long userId, CommentCommand command);
    //댓글 수정
    void updateComment(Long userId, CommentUpdateCommand command);
    //댓글 삭제
    void deleteComment(Long userId,Long commentId,Long postId);

    //댓글조회
    List<PostComments> readComments(Long postId);
}
