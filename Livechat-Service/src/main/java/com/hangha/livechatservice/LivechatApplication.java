package com.hangha.livechatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.hangha.livechatservice",})
@EnableDiscoveryClient
public class LivechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivechatApplication.class, args);
    }

}
