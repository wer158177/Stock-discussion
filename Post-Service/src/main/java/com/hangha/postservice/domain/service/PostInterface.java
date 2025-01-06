package com.hangha.postservice.domain.service;


import com.hangha.postservice.application.command.PostUpdateCommand;
import com.hangha.postservice.application.command.PostWriteCommand;
import com.hangha.postservice.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostInterface {
    //게시글작성
    Long writePost(PostWriteCommand command);
    //게시글 수정
    void updatePost(PostUpdateCommand command);
    //게시글 삭제
    void deletePost(Long userId,Long postId);

    // 게시글 조회 (페이징)
    Page<Post> findAllPosts(Pageable pageable);
}
