package com.github.dragonetail.service1.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(
                        new SimpleClientHttpRequestFactory()));

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }

        interceptors.add(new RestTemplateInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}