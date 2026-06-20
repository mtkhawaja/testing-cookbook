package com.muneebkhawaja.testing.cookbook.web;

import java.net.http.HttpClient;
import java.util.UUID;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.api.EventsApi;
import com.muneebkhawaja.web.generated.model.Event;
import com.muneebkhawaja.web.generated.model.EventUpsertRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// WireMock variant of the client-facing tests. Instead of running the real [EventController], a
/// stubbed Events server is started by `@WireMockTest`; a Spring `RestClient` points at it. This
/// exercises *client-side* HTTP behaviour — request building, response/error handling — and verifies
/// the outgoing request, which is WireMock's main strength.
@WireMockTest
class EventApiWireMockTest {

    private static RestClient clientFor(final WireMockRuntimeInfo wireMock) {
        // Force HTTP/1.1: the JDK client defaults to HTTP/2, which WireMock cancels (RST_STREAM)
        // on requests that carry a body.
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        return RestClient.builder()
                .baseUrl(wireMock.getHttpBaseUrl())
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    private static String eventJson(final UUID id, final String title) {
        return """
                {"id":"%s","title":"%s","severity":"INFO"}""".formatted(id, title);
    }

    @DisplayName("Should send the event and return the created body When creating a new event")
    @Test
    void shouldSendTheEventAndReturnTheCreatedBodyWhenCreatingANewEvent(final WireMockRuntimeInfo wireMock) {
        final UUID createdId = UUID.randomUUID();
        stubFor(post(urlEqualTo(EventsApi.PATH_CREATE_EVENT))
                .withRequestBody(matchingJsonPath("$.title", equalTo("alpha-it")))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(eventJson(createdId, "alpha-it"))));
        final EventUpsertRequest request = TestEvents.upsertRequest("alpha-it");

        final Event created = clientFor(wireMock)
                .post()
                .uri(EventsApi.PATH_CREATE_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Event.class);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(createdId);
        assertThat(created.getTitle()).isEqualTo("alpha-it");
        verify(postRequestedFor(urlEqualTo(EventsApi.PATH_CREATE_EVENT))
                .withRequestBody(matchingJsonPath("$.severity", equalTo("INFO"))));
    }

    @DisplayName("Should return the event When the stubbed server has it")
    @Test
    void shouldReturnTheEventWhenTheStubbedServerHasIt(final WireMockRuntimeInfo wireMock) {
        final UUID eventId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/events/" + eventId))
                .willReturn(okJson(eventJson(eventId, "alpha-it"))));

        final Event fetched = clientFor(wireMock)
                .get()
                .uri(EventsApi.PATH_GET_EVENT, eventId)
                .retrieve()
                .body(Event.class);

        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(eventId);
        assertThat(fetched.getTitle()).isEqualTo("alpha-it");
    }

    @DisplayName("Should raise not found When the stubbed server returns 404")
    @Test
    void shouldRaiseNotFoundWhenTheStubbedServerReturns404(final WireMockRuntimeInfo wireMock) {
        final UUID eventId = UUID.randomUUID();
        stubFor(get(urlEqualTo("/events/" + eventId))
                .willReturn(aResponse().withStatus(404)));
        final RestClient client = clientFor(wireMock);

        assertThatThrownBy(() -> client.get()
                .uri(EventsApi.PATH_GET_EVENT, eventId)
                .retrieve()
                .body(Event.class))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}
