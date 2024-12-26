package com.hangha.commentservice.domain.service;

import com.hangha.stockdiscussion.post_comments.domain.entity.CommentLikes;
import com.hangha.stockdiscussion.post_comments.domain.entity.PostComments;
import com.hangha.commentservice.domain.repository.CommentLikesRepository;
import com.hangha.commentservice.domain.repository.CommentsRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentLikesService {

    private final CommentLikesRepository commentLikesRepository;
    private final CommentsRepository commentsRepository;

    public CommentLikesService(CommentLikesRepository commentLikesRepository, CommentsRepository commentsRepository) {
        this.commentLikesRepository = commentLikesRepository;
        this.commentsRepository = commentsRepository;
    }


    public void likeComment(Long commentId, Long userId) {
        PostComments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (commentLikesRepository.existsByCommentAndUserId(comment, userId)) {
            throw new RuntimeException("이미 좋아요를 누른 댓글입니다.");
        }

        CommentLikes commentLike = new CommentLikes(comment, userId);
        commentLikesRepository.save(commentLike);
    }


    public void unlikeComment(Long commentId, Long userId) {
        PostComments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        CommentLikes commentLike = commentLikesRepository.findByCommentAndUserId(comment, userId)
                .orElseThrow(() -> new RuntimeException("좋아요를 누르지 않은 댓글입니다."));

        commentLikesRepository.delete(commentLike);
    }
}
