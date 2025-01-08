package com.hangha.userservice.domain.Service;

import com.hangha.userservice.domain.entity.Following;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.repository.FollowingRepository;
import com.hangha.userservice.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowingService {
    private final FollowingRepository followingRepository;
    private final UserRepository userRepository;

    public FollowingService(FollowingRepository followingRepository, UserRepository userRepository) {
        this.followingRepository = followingRepository;
        this.userRepository = userRepository;
    }

    public void follow(Long followerId, Long followingId) {
        // 자기 자신을 팔로우하는지 검사
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        validateUsersExist(followerId, followingId);

        if (isAlreadyFollowing(followerId, followingId)) {
            throw new IllegalStateException("이미 팔로우하고 있습니다.");
        }

        Following following = new Following(followerId, followingId);
        followingRepository.save(following);
        updateFollowCount(followerId, followingId, true);
    }

    public void unfollow(Long followerId, Long followingId) {
        validateUsersExist(followerId, followingId);

        Following following = followingRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new NotFoundException("팔로우 관계가 존재하지 않습니다."));

        followingRepository.delete(following);
        updateFollowCount(followerId, followingId, false);
    }

    @Transactional
    public List<Long> getFollowing(Long userId) {
        validateUserExists(userId);
        return followingRepository.findAllByFollowerId(userId).stream()
                .map(Following::getFollowingId)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> getFollowers(Long userId, Long cursor, int size) {
        validateUserExists(userId);
        Pageable pageable = PageRequest.of(0, size);
        return followingRepository.findFollowersByFollowingId(userId, cursor, pageable);
    }

    private void updateFollowCount(Long followerId, Long followingId, boolean isFollow) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("팔로워를 찾을 수 없습니다."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new NotFoundException("팔로잉 대상을 찾을 수 없습니다."));

        if (isFollow) {
            follower.incrementFollowingCount();
            following.incrementFollowerCount();
        } else {
            follower.decrementFollowingCount();
            following.decrementFollowerCount();
        }
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

    private boolean isAlreadyFollowing(Long followerId, Long followingId) {
        return followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}