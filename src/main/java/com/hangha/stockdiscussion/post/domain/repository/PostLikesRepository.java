package com.hangha.stockdiscussion.post.domain.repository;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes,Long> {
    // 게시글과 유저 ID로 좋아요 여부 확인
    boolean existsByPostAndUserId(Post post, Long userId);

    // 게시글과 유저 ID로 좋아요 찾기
    Optional<PostLikes> findByPostAndUserId(Post post, Long userId);
}
