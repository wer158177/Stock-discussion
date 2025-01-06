package com.hangha.postservice.domain.repository;

import com.hangha.postservice.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);


    @Query("SELECT p.id FROM Post p WHERE p.id NOT IN (:excludedPostIds) ORDER BY RAND()")
    List<Long> findRandomPostsExcluding(@Param("excludedPostIds") List<Long> excludedPostIds, Pageable pageable);


    @Query("SELECT p FROM Post p")
    Page<Post> findAllPosts(Pageable pageable);

}
