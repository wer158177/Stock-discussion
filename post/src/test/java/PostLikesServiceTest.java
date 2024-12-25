
import com.hangha.domain.entity.Post;
import com.hangha.domain.entity.PostLikes;
import com.hangha.domain.repository.PostLikesRepository;
import com.hangha.domain.repository.PostRepository;
import com.hangha.domain.service.PostLikesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostLikesServiceTest {

    @Mock
    private PostLikesRepository postLikesRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikesService postLikesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addLike_ShouldAddLikeWhenNotAlreadyLiked() {
        // Given: 게시글 ID와 사용자 ID를 설정하고, 게시글이 존재하며 좋아요가 중복되지 않음을 Mock 설정
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikesRepository.existsByPostAndUserId(post, userId)).thenReturn(false);

        // When: 좋아요 추가 메서드 호출
        postLikesService.addLike(postId, userId);

        // Then: 좋아요가 저장되었는지 검증
        verify(postLikesRepository, times(1)).save(any(PostLikes.class));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenAlreadyLiked() {
        // Given: 게시글 ID와 사용자 ID를 설정하고, 게시글이 존재하며 좋아요가 이미 눌린 상태를 Mock 설정
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikesRepository.existsByPostAndUserId(post, userId)).thenReturn(true);

        // When & Then: 좋아요 중복 예외 발생 확인
        assertThatThrownBy(() -> postLikesService.addLike(postId, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");

        // 좋아요 저장이 호출되지 않았는지 검증
        verify(postLikesRepository, never()).save(any(PostLikes.class));
    }

    @Test
    void removeLike_ShouldRemoveLikeWhenLiked() {
        // Given: 게시글 ID와 사용자 ID를 설정하고, 게시글과 좋아요가 존재하는 상태를 Mock 설정
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();
        PostLikes postLike = new PostLikes(post, userId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikesRepository.findByPostAndUserId(post, userId)).thenReturn(Optional.of(postLike));

        // When: 좋아요 삭제 메서드 호출
        postLikesService.removeLike(postId, userId);

        // Then: 좋아요가 삭제되었는지 검증
        verify(postLikesRepository, times(1)).delete(postLike);
    }

    @Test
    void removeLike_ShouldThrowExceptionWhenNotLiked() {
        // Given: 게시글 ID와 사용자 ID를 설정하고, 게시글은 존재하지만 좋아요가 없는 상태를 Mock 설정
        Long postId = 1L;
        Long userId = 1L;
        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikesRepository.findByPostAndUserId(post, userId)).thenReturn(Optional.empty());

        // When & Then: 좋아요가 없을 때 예외 발생 확인
        assertThatThrownBy(() -> postLikesService.removeLike(postId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("좋아요를 누르지 않았습니다.");

        // 좋아요 삭제가 호출되지 않았는지 검증
        verify(postLikesRepository, never()).delete(any(PostLikes.class));
    }

    @Test
    void findPostById_ShouldThrowExceptionWhenPostNotFound() {
        // Given: 게시글 ID를 설정하고, 게시글이 존재하지 않는 상태를 Mock 설정
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then: 게시글이 없을 때 예외 발생 확인
        assertThatThrownBy(() -> postLikesService.addLike(postId, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글이 존재하지 않습니다.");
    }
}
