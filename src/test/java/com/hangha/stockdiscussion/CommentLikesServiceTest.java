package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.post.post_comments.domain.entity.CommentLikes;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentLikesRepository;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import com.hangha.stockdiscussion.post.post_comments.domain.service.CommentLikesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommentLikesServiceTest {

    @Mock
    private CommentLikesRepository commentLikesRepository; // Mock 댓글 좋아요 저장소

    @Mock
    private CommentsRepository commentsRepository; // Mock 댓글 저장소

    @InjectMocks
    private CommentLikesService commentLikesService; // 댓글 좋아요 서비스 객체

    // 각 테스트 실행 전에 Mock 객체 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 테스트: 댓글 좋아요 성공 시, 저장소에 저장 여부 확인
    @Test
    void likeComment_ShouldSaveLike_WhenNotAlreadyLiked() {
        // Given: 댓글과 사용자 정보 설정
        Long commentId = 1L;
        Long userId = 1L;
        PostComments comment = new PostComments();

        // 댓글과 좋아요 상태를 Mock으로 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentLikesRepository.existsByCommentAndUserId(comment, userId)).thenReturn(false);

        // When: 좋아요 실행
        commentLikesService.likeComment(commentId, userId);

        // Then: 좋아요 저장 검증
        ArgumentCaptor<CommentLikes> captor = ArgumentCaptor.forClass(CommentLikes.class);
        verify(commentLikesRepository, times(1)).save(captor.capture());
        CommentLikes savedLike = captor.getValue();

        assertThat(savedLike.getUserId()).isEqualTo(userId);
        assertThat(savedLike.getComment()).isEqualTo(comment);
    }

    // 테스트: 이미 좋아요를 누른 경우 예외 처리 확인
    @Test
    void likeComment_ShouldThrowException_WhenAlreadyLiked() {
        // Given: 댓글과 사용자 정보 설정
        Long commentId = 1L;
        Long userId = 1L;
        PostComments comment = new PostComments();

        // 댓글과 좋아요 상태를 Mock으로 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentLikesRepository.existsByCommentAndUserId(comment, userId)).thenReturn(true);

        // When & Then: 이미 좋아요 상태인 경우 예외 발생
        assertThatThrownBy(() -> commentLikesService.likeComment(commentId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 좋아요를 누른 댓글입니다.");

        // 저장 메서드가 호출되지 않았는지 검증
        verify(commentLikesRepository, never()).save(any(CommentLikes.class));
    }

    // 테스트: 좋아요 취소 성공 시, 저장소에서 삭제 여부 확인
    @Test
    void unlikeComment_ShouldDeleteLike_WhenExists() {
        // Given: 댓글과 사용자 정보 설정
        Long commentId = 1L;
        Long userId = 1L;
        PostComments comment = new PostComments();
        CommentLikes commentLike = new CommentLikes(comment, userId);

        // 댓글과 좋아요 상태를 Mock으로 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentLikesRepository.findByCommentAndUserId(comment, userId)).thenReturn(Optional.of(commentLike));

        // When: 좋아요 취소 실행
        commentLikesService.unlikeComment(commentId, userId);

        // Then: 좋아요 삭제 검증
        verify(commentLikesRepository, times(1)).delete(commentLike);
    }

    // 테스트: 좋아요 취소 시, 좋아요가 존재하지 않는 경우 예외 처리 확인
    @Test
    void unlikeComment_ShouldThrowException_WhenLikeNotExists() {
        // Given: 댓글과 사용자 정보 설정
        Long commentId = 1L;
        Long userId = 1L;
        PostComments comment = new PostComments();

        // 댓글과 좋아요 상태를 Mock으로 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentLikesRepository.findByCommentAndUserId(comment, userId)).thenReturn(Optional.empty());

        // When & Then: 좋아요가 없는 경우 예외 발생
        assertThatThrownBy(() -> commentLikesService.unlikeComment(commentId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("좋아요를 누르지 않은 댓글입니다.");

        // 삭제 메서드가 호출되지 않았는지 검증
        verify(commentLikesRepository, never()).delete(any(CommentLikes.class));
    }

    // 테스트: 댓글 ID가 잘못된 경우 예외 처리 확인 (좋아요 추가 시)
    @Test
    void likeComment_ShouldThrowException_WhenCommentNotFound() {
        // Given: 댓글 ID 설정
        Long commentId = 1L;
        Long userId = 1L;

        // 댓글이 존재하지 않는 상태로 Mock 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then: 댓글이 없는 경우 예외 발생
        assertThatThrownBy(() -> commentLikesService.likeComment(commentId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
    }

    // 테스트: 댓글 ID가 잘못된 경우 예외 처리 확인 (좋아요 취소 시)
    @Test
    void unlikeComment_ShouldThrowException_WhenCommentNotFound() {
        // Given: 댓글 ID 설정
        Long commentId = 1L;
        Long userId = 1L;

        // 댓글이 존재하지 않는 상태로 Mock 설정
        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then: 댓글이 없는 경우 예외 발생
        assertThatThrownBy(() -> commentLikesService.unlikeComment(commentId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
    }
}
