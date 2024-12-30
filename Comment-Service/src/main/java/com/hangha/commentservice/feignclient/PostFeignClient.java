package com.hangha.commentservice.feignclient;

import com.hangha.commentservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 게시글 서버의 서비스 이름을 지정
@FeignClient(name = "Comment-service", url = "${spring.post-service.url}", configuration = FeignConfig.class)
public interface PostFeignClient {

    @GetMapping("/api/post/{postId}/exists")
    boolean doesPostExist(@PathVariable Long postId);



    @PostMapping("/api/post/{postId}/comments/count")
    void updateCommentCount(@PathVariable Long postId, @RequestParam boolean increment);
}
