package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.domain.Service.FollowingService;
import com.hangha.stockdiscussion.User.domain.entity.Following;
import com.hangha.stockdiscussion.User.domain.repository.FollowingRepository;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowingServiceTest {

    // Mock 객체 정의 (레포지토리)
    @Mock
    private FollowingRepository followingRepository;

    @Mock
    private UserRepository userRepository;

    // 테스트 대상 객체(FollowingService)
    @InjectMocks
    private FollowingService followingService;

    // 각 테스트 실행 전에 Mock 객체 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }

    // 팔로우 성공 테스트
    @Test
    void followUser_success() {
        // Given: 팔로우 요청 정보 (팔로우하는 사용자와 팔로우 당하는 사용자)
        Long followerId = 1L;
        Long followingId = 2L;

        // Mock 설정: 두 사용자 모두 존재하며, 아직 팔로우하지 않음
        when(userRepository.existsById(followerId)).thenReturn(true);
        when(userRepository.existsById(followingId)).thenReturn(true);
        when(followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);

        // When: 팔로우 실행
        followingService.follow(followerId, followingId);

        // Then: 팔로우가 저장되었는지 확인
        verify(followingRepository, times(1)).save(any(Following.class));
    }

    // 중복 팔로우 시 예외 발생 테스트
    @Test
    void followUser_alreadyFollowing_throwsException() {
        // Given: 이미 팔로우한 관계
        Long followerId = 1L;
        Long followingId = 2L;

        // Mock 설정: 두 사용자 존재하며, 이미 팔로우 관계가 있음
        when(userRepository.existsById(followerId)).thenReturn(true);
        when(userRepository.existsById(followingId)).thenReturn(true);
        when(followingRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

        // When & Then: 중복 팔로우 시 예외 발생 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            followingService.follow(followerId, followingId);
        });

        // 예외 메시지 검증
        assertEquals("이미 팔로우한 사용자입니다.", exception.getMessage());
    }

    // 팔로우하려는 사용자가 존재하지 않을 때 예외 발생 테스트
    @Test
    void followUser_userNotFound_throwsException() {
        // Given: 존재하지 않는 사용자
        Long followerId = 1L;
        Long followingId = 2L;

        // Mock 설정: followerId가 데이터베이스에 존재하지 않음
        when(userRepository.existsById(followerId)).thenReturn(false);

        // When & Then: 사용자 없음 예외 발생 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            followingService.follow(followerId, followingId);
        });

        // 예외 메시지 검증
        assertEquals("사용자를 찾을 수 없습니다: 1", exception.getMessage());
    }

    // 언팔로우 성공 테스트
    @Test
    void unfollowUser_success() {
        // Given: 언팔로우 요청 정보
        Long followerId = 1L;
        Long followingId = 2L;

        // Mock 설정: 두 사용자 모두 존재
        when(userRepository.existsById(followerId)).thenReturn(true);
        when(userRepository.existsById(followingId)).thenReturn(true);

        // When: 언팔로우 실행
        followingService.unfollow(followerId, followingId);

        // Then: 언팔로우가 삭제되었는지 확인
        verify(followingRepository, times(1)).deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    // 팔로워 목록 조회 성공 테스트
    @Test
    void getFollowers_success() {
        // Given: 팔로워 조회 대상 사용자
        Long userId = 2L;

        // Mock 설정: 사용자와 팔로워 관계
        List<Following> followers = List.of(
                new Following(1L, userId),
                new Following(3L, userId)
        );
        when(userRepository.existsById(userId)).thenReturn(true);
        when(followingRepository.findAllByFollowingId(userId)).thenReturn(followers);

        // When: 팔로워 목록 조회
        List<Long> result = followingService.getFollowers(userId);

        // Then: 팔로워 목록이 예상대로 반환되었는지 확인
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(3L));
    }

    // 팔로잉 목록 조회 성공 테스트
    @Test
    void getFollowing_success() {
        // Given: 팔로잉 조회 대상 사용자
        Long userId = 1L;

        // Mock 설정: 사용자와 팔로잉 관계
        List<Following> followingList = List.of(
                new Following(userId, 2L),
                new Following(userId, 3L)
        );
        when(userRepository.existsById(userId)).thenReturn(true);
        when(followingRepository.findAllByFollowerId(userId)).thenReturn(followingList);

        // When: 팔로잉 목록 조회
        List<Long> result = followingService.getFollowing(userId);

        // Then: 팔로잉 목록이 예상대로 반환되었는지 확인
        assertEquals(2, result.size());
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }
}