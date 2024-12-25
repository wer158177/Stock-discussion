package com.hangha.domain.repository;

import com.hangha.domain.entity.Post;
import com.hangha.domain.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostStatusRepository extends JpaRepository<PostStatus, Long> {
    Optional<PostStatus> findByPost(Post post);

    // 게시글 ID로 상태 조회
    Optional<PostStatus> findByPostId(Long postId);
}
