package com.muneebkhawaja.testing.cookbook.web;

import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.api.EventsApi;
import com.muneebkhawaja.web.generated.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

///
/// You can also manually create and configure the test client if you don't want to use `@AutoConfigureTestRestTemplate`
/// ``` java
///  // Manual TestRestTemplate
/// var testRestTemplate = new TestRestTemplate(
///     new RestTemplateBuilder().rootUri(baseUrl)
/// );
/// ```
@SpringBootTest(
        classes = TestWebApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestRestTemplate
class EventControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @DisplayName("Should delete event when deleting an event that existed")
    @Test
    void shouldDeleteEventWhenDeletingAnEventThatExisted() {
        // Arrange
        final var create = testRestTemplate.postForEntity(
                EventsApi.PATH_CREATE_EVENT,
                TestEvents.upsertRequest("alpha-it"),
                Event.class
        );
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final var created = Objects.requireNonNull(create.getBody(), "Created event body must not be null");
        final var fetchAfterCreate = testRestTemplate.getForEntity(
                EventsApi.PATH_GET_EVENT,
                Event.class,
                created.getId()
        );
        assertThat(fetchAfterCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Act
        final var delete = testRestTemplate.exchange(
                EventsApi.PATH_DELETE_EVENT,
                HttpMethod.DELETE,
                null,
                Void.class,
                created.getId()
        );
        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        // Assert
        final var fetchAfterDelete = testRestTemplate.getForEntity(
                EventsApi.PATH_GET_EVENT,
                String.class,
                created.getId()
        );
        assertThat(fetchAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}