package com.hangha.postservice;

import com.hangha.postservice.domain.entity.Hashtag;
import com.hangha.postservice.domain.entity.PostHashtag;
import com.hangha.postservice.domain.repository.PostTagRepository;
import com.hangha.postservice.domain.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private PostTagRepository postTagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void getTagsForPost_ShouldReturnTagNames_WhenPostIdIsValid() {
        // Given
        Long postId = 1L;

        Hashtag tag1 = new Hashtag("tag1");
        Hashtag tag2 = new Hashtag("tag2");
        PostHashtag postTag1 = new PostHashtag(postId, tag1, null);
        PostHashtag postTag2 = new PostHashtag(postId, tag2, null);

        when(postTagRepository.findAllByPostId(postId)).thenReturn(List.of(postTag1, postTag2));

        // When
        List<String> tags = tagService.getTagsForPost(postId);

        // Then
        assertThat(tags).hasSize(2);
        assertThat(tags).containsExactlyInAnyOrder("tag1", "tag2");

        verify(postTagRepository, times(1)).findAllByPostId(postId);
    }
}
