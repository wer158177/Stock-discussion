import com.hangha.application.command.RegisterUserCommand;
import com.hangha.domain.Service.UserRegisterService;
import com.hangha.domain.entity.User;
import com.hangha.domain.entity.UserRoleEnum;
import com.hangha.domain.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

    @Mock
    private UserRepository userRepository; // Mock 객체로 UserRepository 생성

    @Mock
    private PasswordEncoder passwordEncoder; // Mock 객체로 PasswordEncoder 생성

    @InjectMocks
    private UserRegisterService userRegisterService; // 테스트 대상 서비스

    @Test
    void registerUser_success() {
        // Given: 테스트에 필요한 데이터 준비
        RegisterUserCommand command = new RegisterUserCommand(
                "testuser",
                "test@example.com",
                "password123",
                "This is a test user",
                "test_image_url",
                false,
                ""
        );

        // Mock 설정
        when(passwordEncoder.encode(command.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: 테스트 대상 메서드 호출
        userRegisterService.registerUser(command);

        // Then: 결과 검증
        // UserRepository의 save 메서드 호출 검증
        verify(userRepository, times(1)).save(any(User.class));

        // 저장된 User 객체 상태 확인
        User savedUser = User.builder()
                .username(command.username())
                .password("encoded_password") // Mocked password
                .email(command.email())
                .intro(command.intro())
                .imageUrl(command.imageUrl())
                .userRole(UserRoleEnum.USER)
                .createdAt(LocalDateTime.now()) // 현재 시간
                .build();

        assertNotNull(savedUser);
        assertEquals(command.username(), savedUser.getUsername());
        assertEquals("encoded_password", savedUser.getPassword());
        assertEquals(command.email(), savedUser.getEmail());
        assertEquals(command.intro(), savedUser.getIntro());
        assertEquals(command.imageUrl(), savedUser.getImageUrl());
        assertEquals(UserRoleEnum.USER, savedUser.getUserRole());
    }



}
