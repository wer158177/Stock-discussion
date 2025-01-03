package com.hangha.userservice.domain.Service;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserinfoService {

    private final UserRepository userRepository;

    public UserinfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponseDto getUserInfo(Long userId){
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return new UserResponseDto(user.getId(),user.getUsername());


    }
}
