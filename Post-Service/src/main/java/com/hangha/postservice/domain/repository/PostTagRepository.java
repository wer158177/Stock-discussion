package com.hangha.postservice.domain.repository;

import com.hangha.postservice.domain.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostHashtag, Long> {
    boolean existsByPostIdAndHashtagId(Long postId, Long hashtagId);
}
