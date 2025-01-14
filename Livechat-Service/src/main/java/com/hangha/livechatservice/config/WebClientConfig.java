package com.hangha.livechatservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder, @Value("${spring.user-service.url}") String userServiceUrl) {
        return builder.baseUrl(userServiceUrl).build();
    }
}
