
import com.hangha.application.PostApplicationService;
import com.hangha.application.command.PostUpdateCommand;
import com.hangha.application.command.PostWriteCommand;
import com.hangha.controller.dto.PostRequestDto;
import com.hangha.controller.dto.PostStatusResponse;
import com.hangha.domain.service.PostLikesService;
import com.hangha.domain.service.PostService;
import com.hangha.domain.service.PostStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostApplicationServiceTest {

    // @Mock: 테스트를 위해 Mock 객체로 대체
    @Mock
    private PostService postService;

    @Mock
    private PostStatusService postStatusService;

    @Mock
    private PostLikesService postLikesService;

    // @InjectMocks: PostApplicationService를 테스트 대상으로 설정
    @InjectMocks
    private PostApplicationService postApplicationService;

    // 각 테스트 실행 전에 Mock 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 테스트: 좋아요 추가
    @Test
    void likePost_ShouldCallAddLikeAndIncrementLikes() {
        // Given: 게시글 ID와 사용자 ID 설정
        Long postId = 1L;
        Long userId = 1L;

        // When: 좋아요 추가 호출
        postApplicationService.likePost(postId, userId);

        // Then: 좋아요 추가와 상태 증가 호출 확인
        verify(postLikesService, times(1)).addLike(postId, userId);
        verify(postStatusService, times(1)).incrementLikes(postId);
    }

    // 테스트: 좋아요 취소
    @Test
    void unlikePost_ShouldCallRemoveLikeAndDecrementLikes() {
        // Given: 게시글 ID와 사용자 ID 설정
        Long postId = 1L;
        Long userId = 1L;

        // When: 좋아요 취소 호출
        postApplicationService.unlikePost(postId, userId);

        // Then: 좋아요 삭제와 상태 감소 호출 확인
        verify(postLikesService, times(1)).removeLike(postId, userId);
        verify(postStatusService, times(1)).decrementLikes(postId);
    }

    // 테스트: 게시글 작성
    @Test
    void postWrite_ShouldCallWritePost() {
        // Given: 사용자 ID와 요청 DTO 설정
        Long userId = 1L;
        PostRequestDto postRequestDto = mock(PostRequestDto.class);
        PostWriteCommand command = new PostWriteCommand(userId, "Title", "Content");
        when(postRequestDto.WriteCommand(userId)).thenReturn(command);

        // When: 게시글 작성 호출
        postApplicationService.postWrite(userId, postRequestDto);

        // Then: 인터페이스의 게시글 작성 호출 확인
        verify(postService, times(1)).writePost(command);
    }

    // 테스트: 게시글 수정
    @Test
    void postUpdate_ShouldCallUpdatePost() {
        // Given: 사용자 ID와 요청 DTO 설정
        Long userId = 1L;
        PostRequestDto postRequestDto = mock(PostRequestDto.class);
        PostUpdateCommand command = new PostUpdateCommand(1L, userId, "Updated Title", "Updated Content");
        when(postRequestDto.updateCommand(userId)).thenReturn(command);

        // When: 게시글 수정 호출
        postApplicationService.postUpdate(userId, postRequestDto);

        // Then: 인터페이스의 게시글 수정 호출 확인
        verify(postService, times(1)).updatePost(command);
    }

    // 테스트: 게시글 삭제
    @Test
    void postDelete_ShouldCallDeletePost() {
        // Given: 사용자 ID와 게시글 ID 설정
        Long userId = 1L;
        Long postId = 1L;

        // When: 게시글 삭제 호출
        postApplicationService.postDelete(userId, postId);

        // Then: 인터페이스의 게시글 삭제 호출 확인
        verify(postService, times(1)).deletePost(postId, userId);
    }

    // 테스트: 게시글 상태 조회
    @Test
    void getPostStatus_ShouldCallGetPostStatusSummary() {
        // Given: 게시글 ID와 반환값 설정
        Long postId = 1L;
        PostStatusResponse response = new PostStatusResponse(10, 5, 100);
        when(postStatusService.getPostStatusSummary(postId)).thenReturn(response);

        // When: 상태 조회 호출
        PostStatusResponse result = postApplicationService.getPostStatus(postId);

        // Then: 상태 조회 호출 확인 및 반환값 검증
        verify(postStatusService, times(1)).getPostStatusSummary(postId);
        assertThat(result.likesCount()).isEqualTo(10);
        assertThat(result.commentsCount()).isEqualTo(5);
        assertThat(result.viewsCount()).isEqualTo(100);
    }

    // 테스트: 좋아요 추가 중 예외 발생
    @Test
    void likePost_ShouldThrowException_WhenAddLikeFails() {
        // Given: 좋아요 중복 예외 설정
        Long postId = 1L;
        Long userId = 1L;
        doThrow(new IllegalStateException("이미 좋아요를 눌렀습니다.")).when(postLikesService).addLike(postId, userId);

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> postApplicationService.likePost(postId, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");

        // 좋아요 상태 업데이트 호출되지 않음 검증
        verify(postStatusService, never()).incrementLikes(postId);
    }
}
