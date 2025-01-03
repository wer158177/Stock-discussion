package com.hangha.postservice.domain.repository;

import com.hangha.postservice.domain.entity.UserFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFeedRepository extends JpaRepository<UserFeed, Long> {
    @Query("SELECT uf.postId FROM UserFeed uf WHERE uf.userId = :userId ORDER BY uf.createdAt DESC")
    List<Long> findLatestPostsForUser(@Param("userId") Long userId, Pageable pageable);
}
