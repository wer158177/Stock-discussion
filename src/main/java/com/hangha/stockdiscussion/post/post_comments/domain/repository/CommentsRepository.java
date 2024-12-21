package com.hangha.stockdiscussion.post.post_comments.domain.repository;

import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<PostComments, Long> {

}
