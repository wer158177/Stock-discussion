package com.hangha.userservice.domain.Service;

import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;



    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Transactional
    public void updateProfile(Long userId, String username, String intro, String profileImage) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 프로필 수정
        user.updateProfile(username, intro, profileImage);

        // 변경사항 저장 (JPA 변경 감지)
        userRepository.save(user);
    }



}

