package com.hangha.mvclivechatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.hangha.mvclivechatservice.infrastructure.client")
public class MvcLiveChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcLiveChatServiceApplication.class, args);
    }

}
