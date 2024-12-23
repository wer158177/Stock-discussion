package com.hangha.stockdiscussion.post.post_comments.domain.service;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;

    public CommentService(CommentsRepository commentsRepository, PostRepository postRepository) {
        this.commentsRepository = commentsRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void writeComment(Long userId, CommentCommand command) {
        Post post = findPostById(command.postId());

        validateParentComment(command.parentId());

        PostComments comment = PostComments.createComment(post, userId, command.content(), command.parentId());
        commentsRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long userId, CommentUpdateCommand command) {
        PostComments comment = findCommentById(command.commentId());

        validateUserAuthorization(comment.getUserId(), userId);

        comment.updateComment(command.content());
        comment.onUpdate();

        commentsRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long userId, Long commentId) {
        PostComments comment = findCommentById(commentId);

        validateUserAuthorization(comment.getUserId(), userId);

        if (comment.getParentId() == null) {
            commentsRepository.deleteRepliesByParentId(comment.getId());
        }

        commentsRepository.delete(comment);
    }

    public List<PostComments> findParentCommentsByPostId(Long postId) {
        return commentsRepository.findByPostIdAndParentIdIsNull(postId);
    }

    public List<PostComments> findRepliesByParentId(Long parentId) {
        return commentsRepository.findRepliesByParentId(parentId);
    }

    // Helper method: Post 조회
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // Helper method: 댓글 조회
    private PostComments findCommentById(Long commentId) {
        return commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    // Helper method: 부모 댓글 검증
    private void validateParentComment(Long parentId) {
        if (parentId != null) {
            PostComments parentComment = findCommentById(parentId);
            if (parentComment.getParentId() != null) {
                throw new RuntimeException("대대댓글은 허용되지 않습니다.");
            }
        }
    }

    // Helper method: 사용자 권한 검증
    private void validateUserAuthorization(Long commentUserId, Long userId) {
        if (!commentUserId.equals(userId)) {
            throw new RuntimeException("자신이 작성한 댓글만 수정/삭제할 수 있습니다.");
        }
    }



}
