package com.hangha.stockdiscussion.post.post_comments.domain.repository;

import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<PostComments, Long> {
    List<PostComments> findByPostId(Long postId);

}
