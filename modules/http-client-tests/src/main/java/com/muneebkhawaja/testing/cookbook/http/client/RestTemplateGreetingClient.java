package com.muneebkhawaja.testing.cookbook.http.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public final class RestTemplateGreetingClient implements GreetingClient {
    private final RestTemplate restTemplate;

    @Autowired
    public RestTemplateGreetingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GreetingResponse getGreeting(String name) {
        URI uri = UriComponentsBuilder.fromPath("/api/greeting")
                .queryParam("name", name)
                .build()
                .toUri();

        return restTemplate.getForObject(uri, GreetingResponse.class);
    }
}