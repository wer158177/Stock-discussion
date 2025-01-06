package com.hangha.postservice.domain.service;


import com.hangha.postservice.domain.entity.Hashtag;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.entity.PostHashtag;
import com.hangha.postservice.domain.repository.PostTagRepository;
import com.hangha.postservice.domain.repository.HashtagRepository;


import com.hangha.postservice.exception.CustomException;
import com.hangha.postservice.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        // 입력 데이터 유효성 검증
        if (tagNames == null || tagNames.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_TAG_LIST);
        }
        if (postId == null) {
            throw new CustomException(ErrorCode.INVALID_POST_ID);
        }


        tagNames.forEach(tagName -> {
            try {
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
            } catch (Exception e) {
                throw new CustomException(ErrorCode.TAG_SAVE_FAILED);
            }
        });
    }


    @Transactional(readOnly = true)
    public List<String> getTagsForPost(Long postId) {
        if (postId == null || postId <= 0) {
            throw new CustomException(ErrorCode.INVALID_POST_ID, "유효하지 않은 게시글 ID입니다.");
        }

        // PostHashtag 리스트를 가져와 Hashtag 이름을 추출
        return postTagRepository.findAllByPostId(postId).stream()
                .map(PostHashtag::getHashtag)
                .map(Hashtag::getName)
                .collect(Collectors.toList());
    }


}

