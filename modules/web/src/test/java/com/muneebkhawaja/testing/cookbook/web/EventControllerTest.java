package com.muneebkhawaja.testing.cookbook.web;

import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.model.Severity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService service;
    @InjectMocks
    private EventController controller;

    @DisplayName("Should create event when valid request")
    @Test
    void shouldCreateEventWhenValidRequest() {
        final var request = TestEvents.upsertRequest("t1", Severity.INFO, OffsetDateTime.now(), "d1");
        final var newlyCreatedEvent = TestEvents.event(UUID.randomUUID(), "t1");
        when(service.create(request)).thenReturn(newlyCreatedEvent);
        final var response = controller.createEvent(request);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isEqualTo(newlyCreatedEvent);
        verify(service).create(request);
    }

    @DisplayName("Should return event When fetching an existing event")
    @Test
    void shouldReturnEventWhenFetchingAnExistingEvent() {
        final var id = UUID.randomUUID();
        final var existingEvent = TestEvents.event(id, "t2");
        when(service.get(id)).thenReturn(Optional.of(existingEvent));
        final var response = controller.getEvent(id);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(existingEvent);
        verify(service).get(id);
    }

    @DisplayName("Should return 404 when fetching an event that does not exist")
    @Test
    void shouldReturn404WhenFetchingAnEventThatDoesNotExist() {
        final var id = UUID.randomUUID();
        when(service.get(id)).thenReturn(Optional.empty());
        final var response = controller.getEvent(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(service).get(id);
    }

    @DisplayName("Should replace event When replacing an existing event")
    @Test
    void shouldReplaceEventWhenReplacingAnExistingEvent() {
        final UUID id = UUID.randomUUID();
        final var upsertRequest = TestEvents.upsertRequest("new");
        final var latestEvent = TestEvents.event(id, "new");
        when(service.replace(id, upsertRequest)).thenReturn(latestEvent);
        final var response = controller.replaceEvent(id, upsertRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(latestEvent);
        verify(service).replace(id, upsertRequest);
    }

    @DisplayName("Should throw ResourceNotFoundException When replacing a non-existent event")
    @Test
    void shouldThrowResourceNotFoundExceptionWhenReplacingANonExistentEvent() {
        final var id = UUID.randomUUID();
        final var upsertRequest = TestEvents.upsertRequest("x");
        when(service.replace(id, upsertRequest)).thenThrow(new ResourceNotFoundException("not found"));
        assertThrows(ResourceNotFoundException.class, () -> controller.replaceEvent(id, upsertRequest));
        verify(service).replace(id, upsertRequest);
    }

    @DisplayName("Should update event fields When patching an existing event")
    @Test
    void shouldUpdateEventFieldsWhenPatchingAnExistingEvent() {
        final var id = UUID.randomUUID();
        final var patchRequest = TestEvents.patchTitle("patched");
        final var latestEvent = TestEvents.event(id, "patched");
        when(service.patch(id, patchRequest)).thenReturn(latestEvent);
        final var response = controller.patchEvent(id, patchRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(latestEvent);
        verify(service).patch(id, patchRequest);
    }

    @DisplayName("Should throw ResourceNotFoundException When patching non-existent event")
    @Test
    void shouldThrowResourceNotFoundExceptionWhenPatchingNonExistentEvent() {
        final var id = UUID.randomUUID();
        final var patchRequest = TestEvents.patchTitle("patched");
        when(service.patch(id, patchRequest)).thenThrow(new ResourceNotFoundException("not found"));
        assertThrows(ResourceNotFoundException.class, () -> controller.patchEvent(id, patchRequest));
        verify(service).patch(id, patchRequest);
    }

    @DisplayName("Should delete event and return no content always When deleting events")
    @Test
    void shouldDeleteEventAndReturnNoContentAlwaysWhenDeletingEvents() {
        final var id = UUID.randomUUID();
        ResponseEntity<Void> response = controller.deleteEvent(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(id);
    }

}