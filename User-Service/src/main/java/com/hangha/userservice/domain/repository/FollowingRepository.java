package com.hangha.userservice.domain.repository;

import com.hangha.userservice.domain.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    List<Following> findAllByFollowerId(Long followerId);
    List<Following> findAllByFollowingId(Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

}
