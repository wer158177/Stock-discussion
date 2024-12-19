package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.domain.Service.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDomainServiceTest {

    private UserDomainService userDomainService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 초기화
        userDomainService = new UserDomainService(passwordEncoder);
    }

    @Test
    void encodePassword_Success() {
        String rawPassword = "securePass123";
        String encodedPassword = "encodedPass123";

        // Mock 동작 설정
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // 동작
        String result = userDomainService.encodePassword(rawPassword);

        // 검증
        assertNotNull(result);
        assertEquals(encodedPassword, result);
        verify(passwordEncoder, times(1)).encode(rawPassword);
    }

    @Test
    @DisplayName("수동 등록한 passwordEncoder를 주입 받아와 문자열 암호화")
    void test1() {
        String password = "Robbie's password";

        // 암호화
        String encodePassword = passwordEncoder.encode(password);
        System.out.println("encodePassword = " + encodePassword);

        String inputPassword = "Robbie";

        // 해시된 비밀번호와 사용자가 입력한 비밀번호를 해싱한 값을 비교
        boolean matches = passwordEncoder.matches(inputPassword, encodePassword);
        System.out.println("matches = " + matches); // 암호화할 때 사용된 값과 다른 문자열과 비교했기 때문에 false
    }
}
