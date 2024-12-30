package com.hangha.commentservice.domain.repository;


import com.hangha.commentservice.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<PostComments, Long> {

    // 특정 게시글의 모든 댓글 조회
    List<PostComments> findByPostId(Long postId);

    // 특정 게시글의 부모 댓글 조회
    @Query("SELECT c FROM PostComments c WHERE c.postId = :postId AND c.parentId IS NULL ORDER BY c.createdAt ASC")
    List<PostComments> findByPostIdAndParentIdIsNull(@Param("postId") Long postId);

    // 특정 부모 댓글에 대한 대댓글 조회
    @Query("SELECT c FROM PostComments c WHERE c.parentId = :parentId ORDER BY c.createdAt ASC")
    List<PostComments> findRepliesByParentId(@Param("parentId") Long parentId);

    // 특정 부모 댓글의 대댓글 삭제
    @Modifying
    @Query("DELETE FROM PostComments c WHERE c.parentId = :parentId")
    void deleteRepliesByParentId(@Param("parentId") Long parentId);

    // 특정 게시글의 모든 댓글 조회 (정렬 포함)
    @Query("SELECT c FROM PostComments c WHERE c.postId = :postId ORDER BY c.createdAt ASC")
    List<PostComments> findAllByPostId(@Param("postId") Long postId);
}
