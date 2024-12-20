package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.domain.entity.UserRoleEnum;
import com.hangha.stockdiscussion.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserRegisterService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserRegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    //관리자페이지는 안만들기에 어드민토큰으로
    private final String ADMIN_TOKEN = "990226";

    @Override
    public User registerUser(RegisterUserCommand command) {
        String encodedPassword = encodePassword(command.password());
        User user = new User(
                null,
                command.username(),
                encodedPassword,
                command.email(),
                command.intro(),
                command.imageUrl(),
                UserRoleEnum.USER,
                LocalDateTime.now()
        );
        return userRepository.save(user);
    }

    @Override
    public boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }



    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
