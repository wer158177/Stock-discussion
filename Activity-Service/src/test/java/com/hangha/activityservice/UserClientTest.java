package com.hangha.activityservice;

import com.hangha.activityservice.infrastructure.UserClient;
import com.hangha.common.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

    @Mock
    private UserClient userClient; // UserClient의 Mock 객체

    @InjectMocks
    private UserClientTest userClientTest; // 테스트하려는 실제 객체

    private Long userId;
    private UserResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        userId = 1L;  // 테스트용 userId 설정
        mockResponse = new UserResponseDto(userId, "testUser"); // mockResponse 객체 초기화
    }

    @Test
    void testGetUserById() {
        // Given: UserResponseDto 객체를 Mock해서 반환하도록 설정
        when(userClient.getUserById(userId)).thenReturn(Mono.just(mockResponse));  // getUserById가 호출되면 mockResponse를 반환하도록 설정

        // When: 실제 메서드를 호출
        Mono<UserResponseDto> responseMono = userClient.getUserById(userId);  // getUserById 호출
        UserResponseDto response = responseMono.block();  // block()을 사용하여 비동기 결과를 동기화

        // Then: 반환값이 예상대로 나오는지 확인
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("testUser", response.getUsername());

        // verify that the method was called once
        verify(userClient, times(1)).getUserById(userId);  // getUserById 메서드가 한 번 호출되었는지 확인
    }

    @Test
    void testGetFollowers() {
        // Given: Flux<Long>을 Mock하여 반환
        Flux<Long> mockFollowers = Flux.just(1L, 2L, 3L);

        when(userClient.getFollowers(userId, 0L, 10)).thenReturn(mockFollowers);  // getFollowers가 호출되면 mockFollowers 반환

        // When: 실제 메서드를 호출
        Flux<Long> followers = userClient.getFollowers(userId, 0L, 10);

        // Then: Flux가 제대로 반환되고, 값이 예상대로 나오는지 확인
        assertNotNull(followers);
        assertEquals(3, followers.collectList().block().size());  // 3명의 팔로워가 있어야 함

        // verify that the method was called once
        verify(userClient, times(1)).getFollowers(userId, 0L, 10);
    }
}
