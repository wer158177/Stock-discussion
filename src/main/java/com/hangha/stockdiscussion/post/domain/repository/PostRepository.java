package com.hangha.stockdiscussion.post.domain.repository;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
