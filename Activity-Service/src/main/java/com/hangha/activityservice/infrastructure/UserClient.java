package com.hangha.activityservice.infrastructure;

import com.hangha.activityservice.config.FeignConfig;
import com.hangha.common.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@FeignClient(name = "User-Service", url = "${spring.user-service.url}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/user/{userId}")
    Mono<UserResponseDto> getUserById(@PathVariable("userId") Long userId);


    //페이징 처리가 필요할수도있음
    @GetMapping("/api/following/{userId}/followers")
    Flux<Long> getFollowers(@PathVariable("userId") Long userId,
                            @RequestParam("cursor") Long cursor,
                            @RequestParam("size") int size);



}
