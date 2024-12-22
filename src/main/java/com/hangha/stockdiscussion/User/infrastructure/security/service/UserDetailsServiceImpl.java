package com.hangha.stockdiscussion.User.infrastructure.security.service;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + email));

        if (!user.isVerified()) { // 유저가 비활성화 상태라면 예외 발생
            throw new UsernameNotFoundException("이메일 인증이 완료되지 않았습니다.");
        }

        return new UserDetailsImpl(user); // 인증된 유저의 정보를 반환
    }



}
