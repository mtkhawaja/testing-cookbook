package com.muneebkhawaja.testing.cookbook.http.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(
        properties = "greeting.service.base-url=https://example.test",
        components = {
                GreetingRestClientConfiguration.class,
                RestClientGreetingClient.class
        }
)
@EnableConfigurationProperties(GreetingClientProperties.class)
class RestClientGreetingClientTest {

    @Autowired
    private GreetingClient greetingClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("GET /api/greeting returns message via RestClient")
    void shouldReturnMessageWhenInvokingHTTPGetRequestAgainstGreetingsViaARestClient() {
        server.expect(requestTo("https://example.test/api/greeting?name=Muneeb"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                                {"message":"Hello, Muneeb"}""",
                        MediaType.APPLICATION_JSON
                ));
        GreetingResponse response = greetingClient.getGreeting("Muneeb");
        assertThat(response.message()).isEqualTo("Hello, Muneeb");
        server.verify();
    }
}