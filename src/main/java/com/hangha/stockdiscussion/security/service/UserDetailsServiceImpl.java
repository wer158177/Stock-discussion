package com.hangha.stockdiscussion.security.service;

import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자 주입
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 이메일로 사용자 로드
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)  // 메서드 이름 수정 확인
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. 이메일: "));
        return new UserDetailsImpl(user);  // 사용자 정보 반환
    }
}
