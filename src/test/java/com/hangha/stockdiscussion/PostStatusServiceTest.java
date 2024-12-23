package com.hangha.stockdiscussion.post.domain.service;

import com.hangha.stockdiscussion.post.controller.dto.PostStatusResponse;
import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.entity.PostStatus;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.domain.repository.PostStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostStatusServiceTest {

    @Mock
    private PostRepository postRepository; // 게시글 저장소 Mock 객체

    @Mock
    private PostStatusRepository postStatusRepository; // 게시글 상태 저장소 Mock 객체

    @InjectMocks
    private PostStatusService postStatusService; // 테스트할 서비스 클래스

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }

    @Test
    void incrementLikes_ShouldIncreaseLikesCount() {
        // Given: 좋아요 증가 테스트 준비
        Long postId = 1L;
        Post post = new Post(); // 게시글 객체 생성
        PostStatus postStatus = new PostStatus(post); // 게시글 상태 객체 생성
        when(postRepository.findById(postId)).thenReturn(Optional.of(post)); // Mock 설정
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 좋아요 증가 메서드 호출
        postStatusService.incrementLikes(postId);

        // Then: 좋아요 수 증가 확인
        assertThat(postStatus.getLikesCount()).isEqualTo(1);
        verify(postStatusRepository, times(1)).save(postStatus);
    }

    @Test
    void decrementLikes_ShouldDecreaseLikesCount() {
        // Given: 좋아요 감소 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        PostStatus postStatus = new PostStatus(post);
        postStatus.incrementLikesCount(); // 초기 좋아요 설정
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 좋아요 감소 메서드 호출
        postStatusService.decrementLikes(postId);

        // Then: 좋아요 수 감소 확인
        assertThat(postStatus.getLikesCount()).isEqualTo(0);
        verify(postStatusRepository, times(1)).save(postStatus);
    }

    @Test
    void updateCommentCount_ShouldIncrementCommentCount() {
        // Given: 댓글 수 증가 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        PostStatus postStatus = new PostStatus(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 댓글 수 증가 메서드 호출
        postStatusService.updateCommentCount(postId, true);

        // Then: 댓글 수 증가 확인
        assertThat(postStatus.getCommentsCount()).isEqualTo(1);
        verify(postStatusRepository, times(1)).save(postStatus);
    }

    @Test
    void updateCommentCount_ShouldDecrementCommentCount() {
        // Given: 댓글 수 감소 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        PostStatus postStatus = new PostStatus(post);
        postStatus.incrementCommentsCount(); // 초기 댓글 추가
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 댓글 수 감소 메서드 호출
        postStatusService.updateCommentCount(postId, false);

        // Then: 댓글 수 감소 확인
        assertThat(postStatus.getCommentsCount()).isEqualTo(0);
        verify(postStatusRepository, times(1)).save(postStatus);
    }

    @Test
    void increaseViewCount_ShouldIncreaseViewCount() {
        // Given: 조회수 증가 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        PostStatus postStatus = new PostStatus(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 조회수 증가 메서드 호출
        postStatusService.increaseViewCount(postId);

        // Then: 조회수 증가 확인
        assertThat(postStatus.getViewsCount()).isEqualTo(1);
        verify(postStatusRepository, times(1)).save(postStatus);
    }

    @Test
    void getPostStatusSummary_ShouldReturnPostStatusResponse() {
        // Given: 상태 요약 정보 반환 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        PostStatus postStatus = new PostStatus(post);
        postStatus.incrementLikesCount();
        postStatus.incrementCommentsCount();
        postStatus.incrementViewsCount();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.of(postStatus));

        // When: 상태 요약 메서드 호출
        PostStatusResponse response = postStatusService.getPostStatusSummary(postId);

        // Then: 상태 요약 반환 값 확인
        assertThat(response.likesCount()).isEqualTo(1);
        assertThat(response.commentsCount()).isEqualTo(1);
        assertThat(response.viewsCount()).isEqualTo(1);
    }

    @Test
    void getPostStatus_ShouldThrowExceptionWhenPostNotFound() {
        // Given: 게시글 조회 실패 테스트 준비
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> postStatusService.incrementLikes(postId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글이 존재하지 않습니다.");
    }

    @Test
    void getPostStatus_ShouldThrowExceptionWhenPostStatusNotFound() {
        // Given: 게시글 상태 조회 실패 테스트 준비
        Long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postStatusRepository.findByPost(post)).thenReturn(Optional.empty());

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> postStatusService.incrementLikes(postId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글 상태가 존재하지 않습니다.");
    }
}
