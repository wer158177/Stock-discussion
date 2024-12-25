
import com.hangha.domain.Service.ProfileService;
import com.hangha.domain.entity.User;
import com.hangha.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

// Mockito를 활용한 단위 테스트 클래스
@ExtendWith(MockitoExtension.class) // Mockito 확장을 활성화하여 Mock 객체를 사용할 수 있게 함
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository; // UserRepository를 Mock 객체로 생성하여 실제 데이터베이스와 분리

    @InjectMocks
    private ProfileService profileService; // 테스트 대상인 ProfileService에 Mock 객체를 주입

    // 프로필 업데이트 성공 시의 동작을 테스트
    @Test
    void updateProfile_success() {
        // Given: 테스트 준비 단계
        Long userId = 1L; // 테스트에서 사용할 사용자 ID

        // Mock User 객체를 생성 (테스트에서 사용할 초기 데이터)
        User user = User.builder()
                .id(userId)
                .username("oldName") // 기존 사용자 이름
                .intro("oldIntro")   // 기존 사용자 자기소개
                .imageUrl("oldImageUrl") // 기존 프로필 이미지 URL
                .build();

        // UserRepository의 findById 메서드 호출 시, user 객체를 반환하도록 Mock 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When: 테스트 대상 메서드 호출
        profileService.updateProfile(userId, "newName", "newIntro", "newImageUrl");

        // 로그로 업데이트된 상태 확인
        System.out.println("Updated Username: " + user.getUsername()); // 업데이트된 사용자 이름 출력
        System.out.println("Updated Intro: " + user.getIntro());       // 업데이트된 자기소개 출력
        System.out.println("Updated ImageUrl: " + user.getImageUrl()); // 업데이트된 프로필 이미지 URL 출력

        // Then: 결과 검증
        // 프로필 필드가 업데이트되었는지 확인
        assertEquals("Username should be updated to 'newName'", "newName", user.getUsername());
        assertEquals("Intro should be updated to 'newIntro'", "newIntro", user.getIntro());
        assertEquals("ImageUrl should be updated to 'newImageUrl'", "newImageUrl", user.getImageUrl());

        // UserRepository의 save 메서드가 호출되었는지 검증
        verify(userRepository).save(user);
    }




}
