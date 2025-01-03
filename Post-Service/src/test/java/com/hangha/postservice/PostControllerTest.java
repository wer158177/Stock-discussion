package com.hangha.postservice;


import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void setup() {
        // 테스트 데이터를 초기화
        postRepository.saveAll(List.of(
                Post.createPost(1L, "Title1", "Content1"),
                Post.createPost(2L, "Title2", "Content2"),
                Post.createPost(3L, "Title3", "Content3")
        ));
    }

    @Test
    public void testGetRandomPosts() throws Exception {
        // 랜덤 게시글 API 호출
        mockMvc.perform(get("/api/post/random")
                        .param("size", "2") // 랜덤 게시글 2개 요청
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // 응답 크기가 2인지 확인
                .andExpect(jsonPath("$[0].title").isNotEmpty()) // 첫 번째 게시글의 제목이 비어있지 않음
                .andExpect(jsonPath("$[1].content").isNotEmpty()); // 두 번째 게시글의 내용이 비어있지 않음
    }
}


