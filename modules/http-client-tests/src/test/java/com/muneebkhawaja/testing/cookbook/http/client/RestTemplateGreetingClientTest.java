package com.muneebkhawaja.testing.cookbook.http.client;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest(
        properties = "greeting.service.base-url=https://example.test",
        components = {
                GreetingRestTemplateConfiguration.class,
                RestTemplateGreetingClient.class
        }
)
@EnableConfigurationProperties(GreetingClientProperties.class)
class RestTemplateGreetingClientTest {
    @Autowired
    private GreetingClient client;
    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("GET /api/greeting returns message via RestTemplate")
    void shouldReturnMessageWhenInvokingHTTPGetRequestAgainstGreetingsViaARestTemplate() {
        server.expect(once(), requestTo("/api/greeting?name=Muneeb"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"message":"Hello, Muneeb"}
                        """, MediaType.APPLICATION_JSON));
        GreetingResponse response = client.getGreeting("Muneeb");
        assertThat(response.message()).isEqualTo("Hello, Muneeb");
        server.verify();
    }
}