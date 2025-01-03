package com.hangha.postservice.domain.service;


import com.hangha.postservice.domain.entity.Hashtag;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.entity.PostHashtag;
import com.hangha.postservice.domain.repository.PostTagRepository;
import com.hangha.postservice.domain.repository.HashtagRepository;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagService {

    private final HashtagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public TagService(HashtagRepository tagRepository, PostTagRepository postTagRepository) {
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    @Transactional
    public void saveTags(List<String> tagNames, Long postId) {
        tagNames.forEach(tagName -> {
            // 태그 저장 또는 조회
            Hashtag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag(tagName);
                        return tagRepository.save(newTag);
                    });

            // 태그-게시글 관계 확인
            boolean exists = postTagRepository.existsByPostIdAndHashtagId(postId, tag.getId());

            if (!exists) {
                // 새로운 태그-게시글 관계 저장
                PostHashtag postTag = PostHashtag.builder()
                        .postId(postId)
                        .hashtag(tag)
                        .createdAt(LocalDateTime.now())
                        .build();
                postTagRepository.save(postTag);

                // 태그 사용 빈도 증가
                tag.incrementUsageCount();
            }
        });
    }
}


