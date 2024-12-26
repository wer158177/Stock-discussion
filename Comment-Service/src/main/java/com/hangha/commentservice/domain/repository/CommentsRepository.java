package com.hangha.commentservice.domain.repository;

import com.hangha.stockdiscussion.post_comments.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<PostComments, Long> {
    List<PostComments> findByPostId(Long postId);


    @Query("SELECT c FROM PostComments c WHERE c.post.id = :postId AND c.parentId IS NULL ORDER BY c.createdAt ASC")
    List<PostComments> findByPostIdAndParentIdIsNull(@Param("postId") Long postId);


    @Query("SELECT c FROM PostComments c WHERE c.parentId = :parentId ORDER BY c.createdAt ASC")
    List<PostComments> findRepliesByParentId(@Param("parentId") Long parentId);


    @Modifying
    @Query("DELETE FROM PostComments c WHERE c.parentId = :parentId")
    void deleteRepliesByParentId(@Param("parentId") Long parentId);


    @Query("SELECT c FROM PostComments c WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<PostComments> findAllByPostId(@Param("postId") Long postId);
}
