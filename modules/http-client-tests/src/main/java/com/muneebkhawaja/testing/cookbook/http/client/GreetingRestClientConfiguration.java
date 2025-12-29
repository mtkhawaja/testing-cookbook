package com.muneebkhawaja.testing.cookbook.http.client;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GreetingRestClientConfiguration {

    @Bean
    RestClient greetingRestClient(final GreetingClientProperties properties, final RestClient.Builder builder) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }

}
