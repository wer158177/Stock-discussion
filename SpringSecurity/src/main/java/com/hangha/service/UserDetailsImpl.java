package com.hangha.service;

import com.hangha.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String email;
    private final String username;
    private final String role;
    private final boolean isVerified;

    private UserDetailsImpl(Long userId, String email, String username, String role, boolean isVerified) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.role = role;
        this.isVerified = isVerified;
    }

    public static UserDetailsImpl fromDto(UserResponseDto dto) {
        if (dto == null || dto.email() == null || dto.username() == null || dto.role() == null) {
            throw new IllegalArgumentException("Invalid UserResponseDto");
        }
        return new UserDetailsImpl(
                dto.userId(),
                dto.email(),
                dto.username(),
                dto.role(),
                dto.isActive()
        );
    }

    public Long getUserId() {
        return userId;
    }



    @Override
    public String getUsername() {
        return email;
    }

    public String getActualUsername() {
        return username; // 사용자 이름 반환
    }

    @Override
    public String getPassword() {
        return null; // 패스워드 필요 없는 경우
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String r : role.split(",")) { // 다중 권한 처리
            authorities.add(new SimpleGrantedAuthority(r.trim()));
        }
        log.debug("사용자 권한: {}", authorities);
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 로직 없음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 로직 없음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 로직 없음
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }
}
