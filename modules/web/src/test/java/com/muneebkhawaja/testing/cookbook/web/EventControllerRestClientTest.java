package com.muneebkhawaja.testing.cookbook.web;

import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.api.EventsApi;
import com.muneebkhawaja.web.generated.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

///
/// You can also manually create and configure the test client if you don't want to use `@AutoConfigureRestTestClient`
///
/// ```java
/// // Manual RestTestClient
/// var restTestClient = RestTestClient
///                 .bindToServer()
///                 .baseUrl(baseUrl)
///                 .build();
/// ```
@SpringBootTest(
        classes = TestWebApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
class EventControllerRestClientTest {

    @Autowired
    private RestTestClient testRestClient;

    @DisplayName("Should create event When creating a new event")
    @Test
    void shouldCreateEventWhenCreatingANewEvent() {
        final var request = TestEvents.upsertRequest("alpha-it");
        final var response = testRestClient
                .post()
                .uri(EventsApi.PATH_CREATE_EVENT)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Event.class);
        assertThat(response.getResponseBody()).isNotNull();
        testRestClient
                .get()
                .uri(EventsApi.PATH_GET_EVENT, response.getResponseBody().getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class)
        ;
    }

    @DisplayName("Should return 404 When replacing a non-existent event")
    @Test
    void shouldReturn404WhenReplacingANonExistentEvent() {
        final var request = TestEvents.upsertRequest("alpha-it");
        testRestClient
                .put()
                .uri(EventsApi.PATH_REPLACE_EVENT, UUID.randomUUID())
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .returnResult(Event.class);
        ;
    }

}