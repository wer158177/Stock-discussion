package com.hangha.postservice.domain.repository;

import com.hangha.postservice.domain.entity.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    @Query("SELECT r.postId FROM Recommendation r WHERE r.userId = :userId ORDER BY r.score DESC")
    List<Long> findTopRecommendationsForUser(@Param("userId") Long userId, Pageable pageable);
}
