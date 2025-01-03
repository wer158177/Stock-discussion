package com.hangha.postservice;

import com.hangha.postservice.controller.dto.PostResponseDto;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.repository.PostRepository;
import com.hangha.postservice.domain.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test") // 테스트 프로파일 활성화
@Transactional
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setup() {
        // 데이터 초기화
        postRepository.deleteAll();

        // 초기 데이터 설정
        List<Post> posts = List.of(
                Post.createPost(1L, "Title 1", "Content 1"),
                Post.createPost(2L, "Title 2", "Content 2"),
                Post.createPost(3L, "Title 3", "Content 3")
        );
        postRepository.saveAll(posts);
    }

    @Test
    void testSavePost() {
        Post post = Post.createPost(4L, "New Title", "New Content");
        Post savedPost = postRepository.save(post);

        assertNotNull(savedPost.getId());
        assertEquals("New Title", savedPost.getTitle());
    }

    @Test
    void testFindAllPosts() {
        List<Post> posts = postRepository.findAll();
        assertEquals(3, posts.size()); // 데이터가 정확히 3개만 있어야 함
    }
}

