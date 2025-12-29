package com.muneebkhawaja.testing.cookbook.http.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ManualHTTPClientTest {
    private MockRestServiceServer restTemplateServer;
    private GreetingClient restTemplateClient;
    private MockRestServiceServer restClientServer;
    private GreetingClient restClientGreetingClient;

    @BeforeEach
    void setUp() {
        // RestTemplate Based Client
        final var restTemplate = new RestTemplate();
        this.restTemplateServer = MockRestServiceServer.bindTo(restTemplate).build();
        this.restTemplateClient = new RestTemplateGreetingClient(restTemplate);
        // RestClient Based Client
        final RestClient.Builder builder = RestClient.builder();
        // Important: bind mock server BEFORE builder.build(), so it can intercept requests.
        this.restClientServer = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder
                .baseUrl("https://restclient.test")
                .build();
        this.restClientGreetingClient = new RestClientGreetingClient(restClient);
    }

    @Test
    @DisplayName("GET /api/greeting returns message via manual RestTemplate")
    void shouldReturnMessageWhenInvokingHTTPGetRequestAgainstGreetingsViaARestTemplate() {
        restTemplateServer.expect(once(), requestTo("/api/greeting?name=Muneeb"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"message":"Hello, Muneeb (RestTemplate)"}
                        """, MediaType.APPLICATION_JSON));
        GreetingResponse response = restTemplateClient.getGreeting("Muneeb");
        assertThat(response.message()).isEqualTo("Hello, Muneeb (RestTemplate)");
        restTemplateServer.verify();
    }

    @Test
    @DisplayName("GET /api/greeting returns message via manual RestClient")
    void shouldReturnMessageWhenInvokingHTTPGetRequestAgainstGreetingsViaARestClient() {
        restClientServer.expect(once(), requestTo("https://restclient.test/api/greeting?name=Muneeb"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"message":"Hello, Muneeb (RestClient)"}
                        """, MediaType.APPLICATION_JSON));
        GreetingResponse response = restClientGreetingClient.getGreeting("Muneeb");
        assertThat(response.message()).isEqualTo("Hello, Muneeb (RestClient)");
        restClientServer.verify();
    }
}