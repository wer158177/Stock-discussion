package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import com.hangha.stockdiscussion.post.post_comments.domain.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentsRepository commentsRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전에 Mock 객체 초기화 및 서비스 생성
        commentsRepository = mock(CommentsRepository.class);
        postRepository = mock(PostRepository.class);
        commentService = new CommentService(commentsRepository, postRepository);
    }

    @Test
    void writeComment_shouldSaveComment() {
        // 댓글 작성 테스트: 댓글이 정상적으로 저장되는지 검증

        Long userId = 1L;
        Long postId = 100L;
        String commentText = "Test comment";

        // 게시글을 찾을 수 있도록 Mock 설정
        Post post = new Post(); // 더미 Post 객체 생성
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId, postId, commentText);

        // 댓글 작성 메서드 호출
        commentService.writeComment(userId, command);

        // 저장된 댓글을 캡처하여 검증
        ArgumentCaptor<PostComments> captor = ArgumentCaptor.forClass(PostComments.class);
        verify(commentsRepository, times(1)).save(captor.capture());
        PostComments savedComment = captor.getValue();
        assertThat(savedComment.getUserId()).isEqualTo(userId); // 작성자 검증
        assertThat(savedComment.getPost()).isEqualTo(post); // 게시글 검증
        assertThat(savedComment.getComment()).isEqualTo(commentText); // 댓글 내용 검증
    }

    @Test
    void updateComment_shouldUpdateExistingComment() {
        // 댓글 수정 테스트: 댓글 내용이 정상적으로 업데이트되는지 검증

        Long userId = 1L;
        Long commentId = 200L;
        Long postId = 100L;
        String updatedText = "Updated comment text";

        // 기존 댓글 Mock 설정
        PostComments comment = PostComments.builder()
                .id(commentId) // id 설정
                .userId(userId)
                .comment("Old comment") // 기존 댓글 내용
                .build();
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // 댓글 수정 요청 생성
        CommentUpdateCommand command = new CommentUpdateCommand(userId, commentId, postId, updatedText);

        // 댓글 수정 메서드 호출
        commentService.updateComment(userId, command);

        // 저장된 댓글의 내용 검증
        verify(commentsRepository, times(1)).save(comment);
        assertThat(comment.getComment()).isEqualTo(updatedText); // 수정된 댓글 내용 검증
    }

    @Test
    void updateComment_shouldThrowExceptionIfNotAuthor() {
        // 댓글 수정 테스트: 작성자가 아닌 경우 예외가 발생하는지 검증

        Long userId = 1L;
        Long commentId = 200L;
        Long postId = 100L;
        Long otherUserId = 2L; // 작성자와 다른 사용자
        String updatedText = "Updated comment text";

        // 다른 사용자가 작성한 댓글 Mock 설정
        PostComments comment = PostComments.builder()
                .id(commentId)
                .userId(otherUserId)
                .comment("Old comment")
                .build();
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // 댓글 수정 요청 생성
        CommentUpdateCommand command = new CommentUpdateCommand(userId, commentId, postId, updatedText);

        // 작성자가 아닌 경우 예외 발생 확인
        assertThrows(RuntimeException.class, () -> commentService.updateComment(userId, command));

        // 댓글 저장이 호출되지 않았는지 검증
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void deleteComment_shouldDeleteIfAuthor() {
        // 댓글 삭제 테스트: 작성자가 댓글 삭제 요청 시 정상적으로 삭제되는지 검증

        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 200L;

        // 게시글 및 댓글 Mock 설정
        Post post = new Post();
        PostComments comment = PostComments.builder()
                .id(commentId)
                .userId(userId)
                .post(post)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // 댓글 삭제 메서드 호출
        commentService.deleteComment(postId, userId, commentId);

        // 댓글 삭제 메서드 호출 검증
        verify(commentsRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowExceptionIfNotAuthor() {
        // 댓글 삭제 테스트: 작성자가 아닌 경우 예외가 발생하는지 검증

        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 200L;
        Long otherUserId = 2L; // 작성자와 다른 사용자

        // 다른 사용자가 작성한 댓글 Mock 설정
        Post post = new Post();
        PostComments comment = PostComments.builder()
                .id(commentId)
                .userId(otherUserId)
                .post(post)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // 작성자가 아닌 경우 예외 발생 확인
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(postId, userId, commentId));

        // 댓글 삭제가 호출되지 않았는지 검증
        verify(commentsRepository, never()).delete(any());
    }
}
