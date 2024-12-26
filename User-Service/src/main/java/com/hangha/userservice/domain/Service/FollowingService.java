package com.hangha.userservice.domain.Service;

import com.hangha.userservice.domain.entity.Following;
import com.hangha.userservice.domain.repository.FollowingRepository;
import com.hangha.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingService {

    private final FollowingRepository followingRepository;
    private final UserRepository userRepository;

    public FollowingService(FollowingRepository followingRepository, UserRepository userRepository) {
        this.followingRepository = followingRepository;
        this.userRepository = userRepository;
    }

    public void follow(Long followerId, Long followingId) {
        validateUsersExist(followerId, followingId);
        if (followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }
        Following following = new Following(followerId, followingId);
        followingRepository.save(following);
    }

    public void unfollow(Long followerId, Long followingId) {
        validateUsersExist(followerId, followingId);
        followingRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<Long> getFollowers(Long userId) {
        validateUserExists(userId);
        return followingRepository.findAllByFollowingId(userId).stream()
                .map(Following::getFollowerId)
                .toList();
    }

    public List<Long> getFollowing(Long userId) {
        validateUserExists(userId);
        return followingRepository.findAllByFollowerId(userId).stream()
                .map(Following::getFollowingId)
                .toList();
    }

    private void validateUsersExist(Long... userIds) {
        for (Long userId : userIds) {
            validateUserExists(userId);
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
    }
}
