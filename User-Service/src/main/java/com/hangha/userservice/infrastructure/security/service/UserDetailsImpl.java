package com.hangha.userservice.infrastructure.security.service;


import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.entity.UserRoleEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 사용자 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자 이메일 반환 (이름 대신)
    @Override
    public String getUsername() {
        return user.getEmail();  // 이메일을 반환하도록 설정
    }


    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getUserRole();
        String authority = role.getAuthority();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));
        log.info("사용자 권한: {}", authority);
        return authorities;
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return user.isVerified(); // 이메일 인증 여부를 활성화 상태로 반환
    }
}
