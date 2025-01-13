//package com.hangha.postservice;
//
//import com.hangha.postservice.controller.dto.PostResponseDto;
//import com.hangha.postservice.domain.repository.PostRepository;
//import com.hangha.postservice.domain.service.PostService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.math.BigInteger;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class) // Mockito 초기화
//class PostServiceTest {
//
//    @Mock
//    private PostRepository postRepository; // Mock 객체 생성
//
//    @InjectMocks
//    private PostService postService; // PostService에 Mock 객체 주입
//
//    @Test
//    void testGetRandomPosts() {
//        // Mock 데이터 생성
//        Object[] row1 = {BigInteger.valueOf(1), "user1", "user1.jpg", "Content 1", "post1.jpg", BigInteger.valueOf(10), Timestamp.valueOf(LocalDateTime.of(2023, 12, 1, 10, 0, 0)), "tag1"};
//        Object[] row2 = {BigInteger.valueOf(1), "user1", "user1.jpg", "Content 1", "post1.jpg", BigInteger.valueOf(10), Timestamp.valueOf(LocalDateTime.of(2023, 12, 1, 10, 0, 0)), "tag2"};
//        Object[] row3 = {BigInteger.valueOf(2), "user2", "user2.jpg", "Content 2", "post2.jpg", BigInteger.valueOf(15), Timestamp.valueOf(LocalDateTime.of(2023, 12, 2, 15, 0, 0)), "tag3"};
//
//        List<Object[]> mockRows = Arrays.asList(row1, row2, row3);
//
//        // Mock 리포지토리 동작 설정
//        Pageable pageable = PageRequest.of(0, 2);
//        Page<Object[]> mockPage = new PageImpl<>(mockRows, pageable, mockRows.size());
//        when(postRepository.findRandomPostsOptimized(pageable)).thenReturn(mockPage);
//
//        // 테스트 메서드 호출
//        Page<PostResponseDto> result = postService.getRandomPosts(pageable);
//
//        // 검증
//        assertEquals(2, result.getContent().size()); // 중복 제거 후 2개의 포스트가 반환되어야 함
//
//        // 첫 번째 포스트 검증
//        PostResponseDto post1 = result.getContent().get(0);
//        assertEquals(1L, post1.getId());
//        assertEquals(Arrays.asList("tag1", "tag2"), post1.getHashtags()); // 태그 병합 검증
//
//        // 두 번째 포스트 검증
//        PostResponseDto post2 = result.getContent().get(1);
//        assertEquals(2L, post2.getId());
//        assertEquals(Arrays.asList("tag3"), post2.getHashtags());
//    }
//}



