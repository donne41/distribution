package com.chatting.service3.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient aiRestClient(@Value("${ai.api.base-url}") String baseUrl,
                                 @Value("${ai.api.key}") String apiKey) {

       var httpClient = HttpClients.custom()
                       .disableAutomaticRetries()
                               .build();

       var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(120)); // Timeout for AI-bot answer

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }


    @Bean
    public RestClient messageRestClient(@Value("${message.service.url}") String messageServiceUrl) {

        var httpClient = HttpClients.custom()
                .disableAutomaticRetries()
                .build();

        var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(messageServiceUrl)
                .build();
    }
}
