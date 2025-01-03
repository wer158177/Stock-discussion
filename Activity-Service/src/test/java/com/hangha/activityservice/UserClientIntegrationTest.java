package com.hangha.activityservice;

import com.hangha.activityservice.infrastructure.UserClient;
import com.hangha.common.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserClientIntegrationTest {


    @Autowired
    private UserClient userClient; // 실제 UserClient를 주입받기

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;  // 테스트용 userId 설정
    }

    @Test
    void testGetUserById() {
        // 실제 API 호출을 통해 데이터를 확인
        Mono<UserResponseDto> responseMono = userClient.getUserById(userId);

        // 응답을 blocking 방식으로 기다려서 결과를 확인
        UserResponseDto response = responseMono.block();

        // Then: 반환값이 예상대로 나오는지 확인
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("testUser", response.getUsername()); // 실제 응답값에 따라 조정
    }
}
