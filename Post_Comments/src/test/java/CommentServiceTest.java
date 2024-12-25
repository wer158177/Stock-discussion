import com.hangha.application.command.CommentCommand;
import com.hangha.application.command.CommentUpdateCommand;
import com.hangha.domain.entity.PostComments;
import com.hangha.domain.repository.CommentsRepository;
import com.hangha.domain.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentsRepository commentsRepository;
    private RestTemplate restTemplate;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentsRepository = mock(CommentsRepository.class);
        restTemplate = mock(RestTemplate.class);
        commentService = new CommentService(commentsRepository, restTemplate);
    }

    // 댓글 작성 테스트
    @Test
    void writeComment_shouldSaveComment() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = null; // 부모 댓글 없음
        String commentText = "Test comment";

        // Mock 설정: 게시글 존재 여부 API 응답
        when(restTemplate.getForObject("http://post-service/posts/" + postId + "/exists", Boolean.class))
                .thenReturn(true);

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId,postId, parentId, commentText);

        // 댓글 작성 메서드 호출
        commentService.writeComment(userId, command);

        // 저장된 댓글 검증
        ArgumentCaptor<PostComments> captor = ArgumentCaptor.forClass(PostComments.class);
        verify(commentsRepository, times(1)).save(captor.capture());
        PostComments savedComment = captor.getValue();

        assertThat(savedComment.getUserId()).isEqualTo(userId);
        assertThat(savedComment.getPostId()).isEqualTo(postId);
        assertThat(savedComment.getContent()).isEqualTo(commentText);
    }

    // 댓글 작성 시 유효하지 않은 게시글 ID 테스트
    @Test
    void writeComment_shouldThrowExceptionForInvalidPostId() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = null;
        String commentText = "Test comment";

        // Mock 설정: 게시글 존재하지 않음
        when(restTemplate.getForObject("http://post-service/posts/" + postId + "/exists", Boolean.class))
                .thenReturn(false);

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId,postId, parentId, commentText);

        // 예외 검증
        assertThrows(RuntimeException.class, () -> commentService.writeComment(userId, command), "게시글을 찾을 수 없습니다.");
    }

    // 대댓글 작성 테스트
    @Test
    void writeReply_shouldSaveReplyWithParentId() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = 200L; // 부모 댓글 ID
        String replyText = "This is a reply";

        // Mock 설정: 게시글 존재 여부 API 응답
        when(restTemplate.getForObject("http://post-service/posts/" + postId + "/exists", Boolean.class))
                .thenReturn(true);

        // Mock 설정: 부모 댓글 존재
        PostComments parentComment = PostComments.builder()
                .id(parentId)
                .postId(postId)
                .content("Parent comment")
                .build();
        when(commentsRepository.findById(parentId)).thenReturn(Optional.of(parentComment));

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(userId,postId, parentId, replyText);

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

    // 부모 댓글 삭제 테스트
    @Test
    void deleteComment_shouldDeleteParentCommentAndReplies() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = 200L;

        // Mock 설정: 부모 댓글 존재
        PostComments parentComment = PostComments.builder()
                .id(parentId)
                .userId(userId)
                .postId(postId)
                .build();

        when(commentsRepository.findById(parentId)).thenReturn(Optional.of(parentComment));

        // 댓글 삭제 메서드 호출
        commentService.deleteComment(postId, userId, parentId);

        // 댓글 삭제 검증
        verify(commentsRepository, times(1)).delete(parentComment);
    }

    // 대대댓글 작성 방지 테스트
    @Test
    void writeComment_shouldThrowExceptionForGrandChildComment() {
        Long userId = 1L;
        Long postId = 100L;
        Long parentId = 200L;
        String replyText = "This is a grandchild comment";

        // Mock 설정: 게시글 존재 여부 API 응답
        when(restTemplate.getForObject("http://post-service/posts/" + postId + "/exists", Boolean.class))
                .thenReturn(true);

        // Mock 설정: 부모 댓글이 이미 대댓글인 경우
        PostComments parentComment = PostComments.builder()
                .id(parentId)
                .postId(postId)
                .parentId(150L) // 부모 댓글의 부모 ID가 존재
                .content("Child comment")
                .build();
        when(commentsRepository.findById(parentId)).thenReturn(Optional.of(parentComment));

        // 댓글 작성 요청 생성
        CommentCommand command = new CommentCommand(postId, parentId,userId, replyText);

        // 예외 검증
        assertThrows(RuntimeException.class, () -> commentService.writeComment(userId, command), "대대댓글은 허용되지 않습니다.");
    }
}
