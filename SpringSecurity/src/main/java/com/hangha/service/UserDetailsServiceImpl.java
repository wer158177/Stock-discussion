package com.hangha.service;

import com.hangha.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final WebClient webClient;

    public UserDetailsServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserResponseDto userResponseDto = webClient.get()
                .uri("/api/user/" + email)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block();

        if (userResponseDto == null || Boolean.FALSE.equals(userResponseDto.isActive())) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없거나 계정이 활성화되지 않았습니다.");
        }

        return UserDetailsImpl.fromDto(userResponseDto);
    }
}
