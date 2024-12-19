package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.Service.UserDomainService;
import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.User.domain.Service.UserJoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserJoinService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainService userDomainService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 초기화
        userService = new UserJoinService(userRepository, userDomainService);
    }

    @Test
    void registerUser_Success() {
        // 준비
        RegisterUserCommand command = new RegisterUserCommand(
                "testUser", "test@test.com", "securePass123", "Hello world!", "uploaded/test.jpg"
        );

        String encodedPassword = "encodedPass123";
        User expectedUser = new User(
                1L, "testUser", encodedPassword, "test@test.com",
                "Hello world!", "uploaded/test.jpg", LocalDateTime.now()
        );

        // Mock 동작 설정
        when(userDomainService.encodePassword(command.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // 서비스 호출
        User result = userService.registerUser(command);

        // 검증
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
        assertEquals(encodedPassword, result.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userDomainService, times(1)).encodePassword(command.password());
    }
}
