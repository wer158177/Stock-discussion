package com.hangha.userservice.domain.Service;

import com.hangha.userservice.domain.entity.Following;

import com.hangha.userservice.domain.repository.FollowingRepository;
import com.hangha.userservice.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowingService {

    private final FollowingRepository followingRepository;

    private final UserRepository userRepository;

    public FollowingService(FollowingRepository followingRepository, UserRepository userRepository) {
        this.followingRepository = followingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        // 자신을 팔로우하는 것을 방지
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자신을 팔로우할 수 없습니다.");
        }

        validateUsersExist(followerId, followingId);

        if (followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }

        Following following = new Following(followerId, followingId);
        followingRepository.save(following);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        // 자신을 팔로우 취소하는 것을 방지
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자신의 팔로우를 취소할 수 없습니다.");
        }

        validateUsersExist(followerId, followingId);
        followingRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    // 커서 기반 페이징으로 팔로워 목록 가져오기
    public List<Long> getFollowers(Long userId, Long cursor, int size) {
        // 커서 기반 페이징 처리
        Pageable pageable = PageRequest.of(0, size);  // 커서 기반으로 데이터를 가져오므로 페이지는 0으로 고정
        return followingRepository.findFollowersByFollowingId(userId, cursor, pageable); // 커서 기준으로 팔로워 조회
    }

    public List<Long> getFollowing(Long userId) {
        validateUserExists(userId);
        return followingRepository.findAllByFollowingId(userId).stream()
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

