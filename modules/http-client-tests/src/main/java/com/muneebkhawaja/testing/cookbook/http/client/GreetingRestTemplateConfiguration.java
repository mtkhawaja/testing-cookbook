package com.muneebkhawaja.testing.cookbook.http.client;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GreetingRestTemplateConfiguration {

    @Bean
    RestTemplate greetingRestTemplate(
            final GreetingClientProperties properties,
            final RestTemplateBuilder builder
    ) {
        return builder
                .rootUri(properties.baseUrl().toString())
                .build();
    }

}
