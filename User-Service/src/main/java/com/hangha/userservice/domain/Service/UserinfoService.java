package com.hangha.userservice.domain.Service;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.userservice.controller.dto.FollowUserinfo;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.repository.FollowingRepository;
import com.hangha.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserinfoService {

    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;

    public UserinfoService(UserRepository userRepository, FollowingRepository followingRepository) {
        this.userRepository = userRepository;
        this.followingRepository = followingRepository;
    }


    public UserResponseDto getUserInfo(Long userId){
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return new UserResponseDto(user.getId(),user.getUsername(),user.getImageUrl());

    }

    public FollowUserinfo getFollowUserInfo(String username,Long currentUserId){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        boolean isFollowing = followingRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());

        return new FollowUserinfo(
                user.getId(),
                user.getUsername(),
                user.getImageUrl(),
                user.getIntro(),
                user.getFollowerCount(),
                user.getFollowingCount(),
                isFollowing
        );

    }




}
