package com.hangha.commentservice.feignclient;


import com.hangha.commentservice.config.FeignConfig;
import com.hangha.common.dto.UserResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${spring.user-service.url}", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/user/{userId}")
    UserResponseDto getUserInfo(@PathVariable("userId") Long userId);
}

