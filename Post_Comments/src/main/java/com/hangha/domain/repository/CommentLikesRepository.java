package com.hangha.domain.repository;

import com.hangha.domain.entity.CommentLikes;
import com.hangha.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    boolean existsByPostCommentsAndUserId(PostComments postComments, Long userId);
    Optional<CommentLikes> findByPostCommentsAndUserId(PostComments postComments, Long userId);
}
