package com.hangha.postservice.domain.repository;

import com.hangha.postservice.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
