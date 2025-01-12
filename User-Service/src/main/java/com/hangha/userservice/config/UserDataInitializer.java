package com.hangha.userservice.config;

import com.hangha.userservice.domain.entity.User;
import com.hangha.userservice.domain.entity.UserRoleEnum;
import com.hangha.userservice.domain.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        List<User> testUsers = new ArrayList<>();

        // 기존 데이터 유지
        testUsers.add(User.builder()
                .id(1L)
                .username("inwook")
                .password("$2a$10$lvXx/c3a1GMIwSCyP10OoO03MdOR6hubNdpgU9AWPBLeJndJdEEyy")
                .email("wer158177@gmail.com")
                .intro(null)
                .imageUrl(null)
                .userRole(UserRoleEnum.USER)
                .createdAt(LocalDateTime.parse("2025-01-07T16:54:39.024431"))
                .build());

        testUsers.add(User.builder()
                .id(2L)
                .username("wer158177")
                .password("$2a$10$Ho8l9OkR3urB9/2CYej0TOfL2NnQYb6YFLYPiKVFwlhQ39edZ5BxK")
                .email("wer158177@naver.com")
                .intro(null)
                .imageUrl(null)
                .userRole(UserRoleEnum.USER)
                .createdAt(LocalDateTime.parse("2025-01-07T17:24:10.884825"))
                .build());

        // 500명의 테스트 유저 추가
        for (int i = 1; i <= 35000; i++) {
            String username = "testuser" + i;
            String email = username + "@example.com";
            String password = "test" + i; // 테스트용 비밀번호

            testUsers.add(User.builder()
                    .id((long) (i + 2)) // ID는 3부터 시작
                    .username(username)
                    .password(password)
                    .email(email)
                    .intro(null)
                    .imageUrl(null)
                    .userRole(UserRoleEnum.USER)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // 데이터 저장
        userRepository.saveAll(testUsers);
        System.out.println("[UserDataInitializer] 500명의 테스트 유저가 추가되었습니다.");
    }
}
