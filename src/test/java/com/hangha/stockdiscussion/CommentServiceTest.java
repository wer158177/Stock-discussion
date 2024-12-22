package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import com.hangha.stockdiscussion.post.post_comments.domain.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentsRepository commentsRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    // 각 테스트 실행 전에 Mock 객체를 초기화하고 서비스 객체를 생성
    @BeforeEach
    void setUp() {
        commentsRepository = mock(CommentsRepository.class);
        postRepository = mock(PostRepository.class);
        commentService = new CommentService(commentsRepository, postRepository);
    }

    // 댓글 작성 테스트: 댓글이 정상적으로 저장되는지 검증
    @Test
    void writeComment_shouldSaveComment() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = null; // 부모 댓글이 없으면 최상위 댓글
        String commentText = "Test comment";

        // Mock 설정: 게시글이 존재한다고 가정
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId, postId, parentId, commentText);

        // 댓글 작성 메서드 호출
        commentService.writeComment(userId, command);

        // 저장된 댓글 캡처
        ArgumentCaptor<PostComments> captor = ArgumentCaptor.forClass(PostComments.class);
        verify(commentsRepository, times(1)).save(captor.capture());
        PostComments savedComment = captor.getValue();

        // 작성된 댓글의 데이터 검증
        assertThat(savedComment.getUserId()).isEqualTo(userId);
        assertThat(savedComment.getPost()).isEqualTo(post);
        assertThat(savedComment.getContent()).isEqualTo(commentText);
    }

    // 댓글 수정 테스트: 댓글 내용이 정상적으로 업데이트되는지 검증
    @Test
    void updateComment_shouldUpdateParentComment() {
        Long userId = 1L;
        Long commentId = 200L;
        Long postId = 100L;
        String updatedText = "Updated parent comment";

        // Mock 설정: 기존 댓글이 존재한다고 가정
        PostComments comment = PostComments.builder()
                .id(commentId)
                .userId(userId)
                .content("Old parent comment")
                .build();
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // 댓글 수정 요청 생성
        CommentUpdateCommand command = new CommentUpdateCommand(userId, commentId, postId, null, updatedText);

        // 댓글 수정 메서드 호출
        commentService.updateComment(userId, command);

        // 댓글 저장 검증
        verify(commentsRepository, times(1)).save(comment);
        assertThat(comment.getContent()).isEqualTo(updatedText);
    }

    // 대댓글 작성 테스트: 대댓글이 부모 댓글과 올바르게 연관되는지 검증
    @Test
    void writeReply_shouldSaveReplyWithParentId() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = 200L; // 부모 댓글 ID
        String replyText = "This is a reply";

        // Mock 설정: 게시글과 부모 댓글이 존재한다고 가정
        Post post = new Post();
        PostComments parentComment = PostComments.builder()
                .id(parentId)
                .post(post)
                .content("Parent comment")
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(parentId)).thenReturn(Optional.of(parentComment));

        // 대댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId, postId, parentId, replyText);

        // 대댓글 작성 메서드 호출
        commentService.writeComment(userId, command);

        // 저장된 대댓글 검증
        ArgumentCaptor<PostComments> captor = ArgumentCaptor.forClass(PostComments.class);
        verify(commentsRepository, times(1)).save(captor.capture());
        PostComments savedReply = captor.getValue();

        assertThat(savedReply.getUserId()).isEqualTo(userId);
        assertThat(savedReply.getParentId()).isEqualTo(parentId);
        assertThat(savedReply.getContent()).isEqualTo(replyText);
    }

    // 부모 댓글 삭제 테스트: 부모 댓글 삭제 시 연관된 대댓글도 처리되는지 검증
    @Test
    void deleteComment_shouldDeleteParentCommentAndReplies() {
        // Given: Mock 설정
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = 200L;

        Post post = new Post();
        PostComments parentComment = PostComments.builder()
                .id(parentId)
                .userId(userId)
                .post(post)
                .build();
        PostComments reply = PostComments.builder()
                .id(300L)
                .parentId(parentId)
                .userId(userId)
                .post(post)
                .build();

        // Mock 설정: 부모 댓글과 연관된 대댓글이 존재
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(parentId)).thenReturn(Optional.of(parentComment));
        when(commentsRepository.findRepliesByParentId(parentId)).thenReturn(List.of(reply));

        // When: 댓글 삭제 호출
        commentService.deleteComment(postId, userId, parentId);

        // Then: 대댓글 삭제 검증
        verify(commentsRepository, times(1)).deleteRepliesByParentId(parentId);

        // 부모 댓글 삭제 검증
        ArgumentCaptor<PostComments> captor = ArgumentCaptor.forClass(PostComments.class);
        verify(commentsRepository, times(1)).delete(captor.capture());

        // 삭제된 부모 댓글 검증
        PostComments deletedComment = captor.getValue();
        assertThat(deletedComment).isEqualTo(parentComment);
    }



    // 대댓글 삭제 테스트: 작성자가 대댓글 삭제 요청 시 정상적으로 삭제되는지 검증
    @Test
    void deleteReply_shouldDeleteIfAuthor() {
        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 300L;
        Long parentId = 200L;

        // Mock 설정: 대댓글이 존재한다고 가정
        Post post = new Post();
        PostComments reply = PostComments.builder()
                .id(commentId)
                .userId(userId)
                .parentId(parentId)
                .post(post)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(reply));

        // 대댓글 삭제 메서드 호출
        commentService.deleteComment(postId, userId, commentId);

        // 대댓글 삭제 검증
        verify(commentsRepository, times(1)).delete(reply);
    }


    @Test
    void findParentCommentsByPostId_shouldReturnOnlyParentComments() {
        // 부모 댓글만 조회하는 테스트

        // Given
        Long postId = 1L; // 테스트용 게시글 ID

        // 게시글 객체 생성 및 ID 설정
        Post post = new Post(); // 실제 게시글 객체 생성
        ReflectionTestUtils.setField(post, "id", postId); // Reflection을 사용하여 ID 설정

        // 부모 댓글 1 생성
        PostComments parentComment1 = PostComments.builder()
                .id(101L) // 댓글 ID
                .post(post) // 게시글과 연결
                .parentId(null) // 부모 댓글이므로 parentId는 null
                .userId(1L) // 작성자 ID
                .content("Parent comment 1") // 댓글 내용
                .createdAt(LocalDateTime.now()) // 생성 시간
                .build();

        // 부모 댓글 2 생성
        PostComments parentComment2 = PostComments.builder()
                .id(102L) // 댓글 ID
                .post(post) // 게시글과 연결
                .parentId(null) // 부모 댓글이므로 parentId는 null
                .userId(2L) // 작성자 ID
                .content("Parent comment 2") // 댓글 내용
                .createdAt(LocalDateTime.now()) // 생성 시간
                .build();

        // Mock 설정: 주어진 게시글 ID로 부모 댓글만 반환
        when(commentsRepository.findByPostIdAndParentIdIsNull(postId))
                .thenReturn(List.of(parentComment1, parentComment2));

        // When
        // 부모 댓글 조회 메서드 호출
        List<SimpleCommentResponseDto> result = commentService.findParentCommentsByPostId(postId);

        // Then
        // 부모 댓글 수 검증
        assertThat(result).hasSize(2);

        // 첫 번째 댓글 검증
        assertThat(result.get(0).getId()).isEqualTo(101L);
        assertThat(result.get(0).getContent()).isEqualTo("Parent comment 1");

        // 두 번째 댓글 검증
        assertThat(result.get(1).getId()).isEqualTo(102L);
        assertThat(result.get(1).getContent()).isEqualTo("Parent comment 2");
    }

    @Test
    void findRepliesByParentId_shouldReturnOnlyReplies() {
        // 특정 부모 댓글에 대한 대댓글 조회 테스트

        // Given
        Long parentId = 101L; // 테스트용 부모 댓글 ID

        // 대댓글 1 생성
        PostComments reply1 = PostComments.builder()
                .id(201L) // 대댓글 ID
                .parentId(parentId) // 부모 댓글 ID 설정
                .userId(3L) // 작성자 ID
                .content("Reply 1") // 대댓글 내용
                .createdAt(LocalDateTime.now()) // 생성 시간
                .build();

        // 대댓글 2 생성
        PostComments reply2 = PostComments.builder()
                .id(202L) // 대댓글 ID
                .parentId(parentId) // 부모 댓글 ID 설정
                .userId(4L) // 작성자 ID
                .content("Reply 2") // 대댓글 내용
                .createdAt(LocalDateTime.now()) // 생성 시간
                .build();

        // Mock 설정: 주어진 부모 댓글 ID로 대댓글만 반환
        when(commentsRepository.findRepliesByParentId(parentId))
                .thenReturn(List.of(reply1, reply2));

        // When
        // 대댓글 조회 메서드 호출
        List<SimpleCommentResponseDto> result = commentService.findRepliesByParentId(parentId);

        // Then
        // 대댓글 수 검증
        assertThat(result).hasSize(2);

        // 첫 번째 대댓글 검증
        assertThat(result.get(0).getId()).isEqualTo(201L);
        assertThat(result.get(0).getContent()).isEqualTo("Reply 1");

        // 두 번째 대댓글 검증
        assertThat(result.get(1).getId()).isEqualTo(202L);
        assertThat(result.get(1).getContent()).isEqualTo("Reply 2");
    }






}
