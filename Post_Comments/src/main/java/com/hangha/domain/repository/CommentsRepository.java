package com.hangha.domain.repository;

import com.hangha.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<PostComments, Long> {
    List<PostComments> findByPostId(Long postId);


    // 수정된 방식
    @Query("SELECT c FROM PostComments c WHERE c.postId = :postId AND c.parentId IS NULL ORDER BY c.createdAt ASC")
    List<PostComments> findByPostIdAndParentIdIsNull(@Param("postId") Long postId);


    @Query("SELECT c FROM PostComments c WHERE c.parentId = :parentId ORDER BY c.createdAt ASC")
    List<PostComments> findRepliesByParentId(@Param("parentId") Long parentId);


    @Modifying
    @Query("DELETE FROM PostComments c WHERE c.parentId = :parentId")
    void deleteRepliesByParentId(@Param("parentId") Long parentId);


}
