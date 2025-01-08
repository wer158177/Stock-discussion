package com.hangha.commentservice.domain.repository;


import com.hangha.commentservice.domain.entity.CommentLikes;
import com.hangha.commentservice.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    boolean existsByCommentAndUserId(PostComments comment, Long userId);
    Optional<CommentLikes> findByCommentAndUserId(PostComments comment, Long userId);


    @Query("SELECT c.comment.id FROM CommentLikes c WHERE c.userId = :userId AND c.comment.id IN :commentIds")
    List<Long> findLikedCommentIds(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);
}
