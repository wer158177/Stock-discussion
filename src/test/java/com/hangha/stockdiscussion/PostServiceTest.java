package com.hangha.stockdiscussion;


import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.domain.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository; // PostRepository의 Mock 객체
    private PostService postService; // 테스트 대상인 PostService 객체

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 Mock 객체 및 서비스 초기화
        postRepository = mock(PostRepository.class); // Mock 객체 생성
        postService = new PostService(postRepository); // PostService에 Mock 주입
    }

    @Test
    void writePost_shouldSavePost() {
        // Given: 게시글 작성 요청 생성
        PostWriteCommand command = new PostWriteCommand(1L, "새로운 제목", "새로운 내용");

        // When: PostService의 writePost 호출
        postService.writePost(command);

        // Then: PostRepository.save가 호출되었는지 검증
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class); // 저장된 Post 객체를 캡처
        verify(postRepository, times(1)).save(captor.capture()); // save가 1번 호출되었는지 검증
        Post savedPost = captor.getValue(); // 저장된 Post 객체 추출

        // 저장된 게시글의 필드값 검증
        assertThat(savedPost.getUserId()).isEqualTo(command.userId());
        assertThat(savedPost.getTitle()).isEqualTo(command.title());
        assertThat(savedPost.getContent()).isEqualTo(command.content());
        assertThat(savedPost.getCreatedAt()).isNotNull();
        assertThat(savedPost.getUpdatedAt()).isNotNull();
    }

    @Test
    void updatePost_shouldUpdateExistingPost() {
        // Given: 기존 게시글과 업데이트 요청 생성
        Long postId = 100L;
        Long userId = 1L;

        // 기존 게시글 객체 생성
        Post existingPost = Post.builder()
                .id(postId)
                .userId(userId)
                .title("Old Title")
                .content("Old Content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Mock 설정: PostRepository.findById 호출 시 기존 게시글 반환
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // 업데이트 요청 생성
        PostUpdateCommand command = new PostUpdateCommand(userId, postId, "Updated Content", "Updated Title");

        // When: PostService의 updatePost 호출
        postService.updatePost(command);

        // Then: 게시글이 업데이트되었는지 검증
        assertThat(existingPost.getTitle()).isEqualTo("Updated Title");
        assertThat(existingPost.getContent()).isEqualTo("Updated Content");

        // PostRepository.save가 호출되었는지 검증
        verify(postRepository, times(1)).save(existingPost);
    }

    @Test
    void updatePost_shouldThrowExceptionIfNotAuthor() {
        // Given: 작성자가 아닌 사용자가 게시글을 수정하려는 경우
        Long postId = 100L;
        Long requestUserId = 1L; // 요청 사용자 ID
        Long postAuthorId = 2L; // 게시글 작성자 ID

        // 기존 게시글 객체 생성 (작성자가 다름)
        Post existingPost = Post.builder()
                .id(postId)
                .userId(postAuthorId)
                .title("Old Title")
                .content("Old Content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Mock 설정: PostRepository.findById 호출 시 기존 게시글 반환
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // 업데이트 요청 생성
        PostUpdateCommand command = new PostUpdateCommand(requestUserId, postId, "Updated Content", "Updated Title");

        // When / Then: 예외 발생 여부 및 메시지 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.updatePost(command));
        assertThat(exception.getMessage()).isEqualTo("자신이 작성한 게시글만 수정할 수 있습니다.");

        // PostRepository.save가 호출되지 않았는지 검증
        verify(postRepository, never()).save(any());
    }

    @Test
    void deletePost_shouldDeleteIfAuthor() {
        // Given: 작성자가 게시글을 삭제하려는 경우
        Long postId = 100L;
        Long userId = 1L;

        // 기존 게시글 객체 생성
        Post existingPost = Post.builder()
                .id(postId)
                .userId(userId)
                .title("Some Title")
                .content("Some Content")
                .build();

        // Mock 설정: PostRepository.findById 호출 시 기존 게시글 반환
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // When: PostService의 deletePost 호출
        postService.deletePost(postId, userId);

        // Then: PostRepository.delete가 호출되었는지 검증
        verify(postRepository, times(1)).delete(existingPost);
    }

    @Test
    void deletePost_shouldThrowExceptionIfNotAuthor() {
        // Given: 작성자가 아닌 사용자가 게시글을 삭제하려는 경우
        Long postId = 100L;
        Long userId = 1L;
        Long otherUserId = 2L;

        // 기존 게시글 객체 생성 (작성자가 다름)
        Post existingPost = Post.builder()
                .id(postId)
                .userId(otherUserId)
                .title("Some Title")
                .content("Some Content")
                .build();

        // Mock 설정: PostRepository.findById 호출 시 기존 게시글 반환
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // When / Then: 예외 발생 여부 검증
        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, userId));

        // PostRepository.delete가 호출되지 않았는지 검증
        verify(postRepository, never()).delete(any());
    }
}
