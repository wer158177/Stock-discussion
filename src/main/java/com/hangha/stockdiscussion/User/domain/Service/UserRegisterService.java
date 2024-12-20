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


    @Override
    public boolean validateLogin(String email, String password, HttpServletResponse res) {
        Optional<User> optionalUser = userRepository.findByemail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //JWT 생성 및 쿠키에 저장 후 Response
        String token = jwtUtil.createToken(user.getUsername(),user.getUserRole());
        jwtUtil.addJwtToCookie(token,res);
        return true;  // 로그인 성공
    }



    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
