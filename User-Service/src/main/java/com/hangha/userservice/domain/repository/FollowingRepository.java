package com.hangha.userservice.domain.repository;

import com.hangha.userservice.domain.entity.Following;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT f.followerId FROM Following f WHERE f.followingId = :userId AND f.followerId > :cursor ORDER BY f.followerId ASC")
    List<Long> findFollowersByFollowingId(@Param("userId") Long userId,
                                          @Param("cursor") Long cursor,
                                          Pageable pageable);

    List<Following> findAllByFollowingId(Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

}
