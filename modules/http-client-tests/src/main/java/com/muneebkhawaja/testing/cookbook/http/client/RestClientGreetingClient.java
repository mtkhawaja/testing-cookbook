package com.muneebkhawaja.testing.cookbook.http.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Primary
@Component
public final class RestClientGreetingClient implements GreetingClient {
    private final RestClient restClient;

    @Autowired
    public RestClientGreetingClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public GreetingResponse getGreeting(String name) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/greeting")
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .body(GreetingResponse.class);
    }
}