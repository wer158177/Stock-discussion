package com.hangha.stockdiscussion.post.post_comments.domain.repository;

import com.hangha.stockdiscussion.post.post_comments.domain.entity.CommentLikes;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    boolean existsByCommentAndUserId(PostComments comment, Long userId);
    Optional<CommentLikes> findByCommentAndUserId(PostComments comment, Long userId);
}
