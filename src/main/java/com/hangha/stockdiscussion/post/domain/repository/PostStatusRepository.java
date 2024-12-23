package com.hangha.stockdiscussion.post.domain.repository;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostStatusRepository extends JpaRepository<PostStatus, Long> {
    Optional<PostStatus> findByPost(Post post);

    // 게시글 ID로 상태 조회
    Optional<PostStatus> findByPostId(Long postId);
}
