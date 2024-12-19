package com.hangha.stockdiscussion.User.domain.Service;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {

    private final PasswordEncoder passwordEncoder;

    public UserDomainService(PasswordEncoder passwordEncoder ) {
        this.passwordEncoder = passwordEncoder;
    }

    //비밀번호 암호와
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}