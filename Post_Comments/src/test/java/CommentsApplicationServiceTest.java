//
//import com.hangha.application.CommentsApplicationService;
//import com.hangha.application.command.CommentCommand;
//import com.hangha.controller.dto.CommentsRequestDto;
//import com.hangha.controller.dto.SimpleCommentResponseDto;
//import com.hangha.domain.entity.PostComments;
//import com.hangha.domain.service.CommentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class CommentsApplicationServiceTest {
//
//    @Mock
//    private CommentService commentService; // 댓글 관련 도메인 서비스 목(mock).
//
//    @Mock
//    private PostStatusService postStatusService; // 게시글 상태 업데이트 관련 서비스 목(mock).
//
//    @InjectMocks
//    private CommentsApplicationService commentsApplicationService;
//    // 테스트 대상인 CommentsApplicationService에 목(mock) 주입.
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        // 테스트 실행 전 Mock 객체 초기화.
//    }
//
//    @Test
//    void commentWrite_shouldCallCommentServiceAndPostStatusService() {
//        // CommentsApplicationService의 댓글 작성 로직을 테스트.
//
//        // Given
//        Long userId = 1L;
//        CommentsRequestDto requestDto = new CommentsRequestDto();
//        ReflectionTestUtils.setField(requestDto, "postId", 100L);
//        // ReflectionTestUtils를 사용하여 DTO의 필드를 설정.
//        ReflectionTestUtils.setField(requestDto, "content", "Test comment");
//
//        // When
//        commentsApplicationService.commentWrite(userId, requestDto);
//
//        // Then
//        verify(commentService, times(1)).writeComment(eq(userId), any(CommentCommand.class));
//        // commentService의 writeComment 메서드가 한 번 호출되었는지 검증.
//        verify(postStatusService, times(1)).updateCommentCount(eq(100L), eq(true));
//        // postStatusService의 updateCommentCount가 호출되었는지 검증.
//    }
//
//    @Test
//    void commentDelete_shouldCallCommentServiceAndPostStatusService() {
//        // CommentsApplicationService의 댓글 삭제 로직을 테스트.
//
//        // Given
//        Long userId = 1L;
//        Long commentId = 200L;
//        Long postId = 100L;
//
//        // When
//        commentsApplicationService.commentDelete(userId, commentId, postId);
//
//        // Then
//        verify(commentService, times(1)).deleteComment(eq(userId), eq(commentId), eq(postId));
//        // commentService의 deleteComment 메서드 호출 검증.
//        verify(postStatusService, times(1)).updateCommentCount(eq(postId), eq(false));
//        // postStatusService의 updateCommentCount 호출 검증.
//    }
//
//    @Test
//    void getParentComments_shouldConvertToSimpleCommentResponseDto() {
//        // 부모 댓글 조회 로직을 테스트하며, DTO로 올바르게 변환되는지 확인.
//
//        // Given
//        Long postId = 100L;
//        PostComments comment1 = PostComments.builder()
//                .id(1L)
//                .content("Parent comment 1")
//                .userId(1L)
//                .createdAt(LocalDateTime.now())
//                .build();
//        PostComments comment2 = PostComments.builder()
//                .id(2L)
//                .content("Parent comment 2")
//                .userId(2L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(commentService.findParentCommentsByPostId(postId)).thenReturn(List.of(comment1, comment2));
//        // commentService에서 부모 댓글 리스트를 반환하도록 Mock 설정.
//
//        // When
//        List<SimpleCommentResponseDto> result = commentsApplicationService.getParentComments(postId);
//
//        // Then
//        assertThat(result).hasSize(2); // 반환된 리스트의 크기를 검증.
//        assertThat(result.get(0).getContent()).isEqualTo("Parent comment 1"); // 첫 번째 댓글 내용 검증.
//        assertThat(result.get(1).getContent()).isEqualTo("Parent comment 2"); // 두 번째 댓글 내용 검증.
//    }
//
//    @Test
//    void getReplies_shouldConvertToSimpleCommentResponseDto() {
//        // 대댓글 조회 로직을 테스트하며, DTO로 올바르게 변환되는지 확인.
//
//        // Given
//        Long parentId = 1L;
//        PostComments reply1 = PostComments.builder()
//                .id(101L)
//                .content("Reply 1")
//                .userId(1L)
//                .createdAt(LocalDateTime.now())
//                .build();
//        PostComments reply2 = PostComments.builder()
//                .id(102L)
//                .content("Reply 2")
//                .userId(2L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(commentService.findRepliesByParentId(parentId)).thenReturn(List.of(reply1, reply2));
//        // commentService에서 대댓글 리스트를 반환하도록 Mock 설정.
//
//        // When
//        List<SimpleCommentResponseDto> result = commentsApplicationService.getReplies(parentId);
//
//        // Then
//        assertThat(result).hasSize(2); // 반환된 리스트의 크기를 검증.
//        assertThat(result.get(0).getContent()).isEqualTo("Reply 1"); // 첫 번째 대댓글 내용 검증.
//        assertThat(result.get(1).getContent()).isEqualTo("Reply 2"); // 두 번째 대댓글 내용 검증.
//    }
//}
