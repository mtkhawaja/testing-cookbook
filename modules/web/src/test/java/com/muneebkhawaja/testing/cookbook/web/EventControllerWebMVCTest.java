package com.muneebkhawaja.testing.cookbook.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.api.EventsApi;
import com.muneebkhawaja.web.generated.model.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
class EventControllerWebMVCTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @MockitoBean
    private EventService service;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Should create event When creating a new event")
    @Test
    void shouldCreateEventWhenCreatingANewEvent() throws Exception {
        final var request = TestEvents.upsertRequest("alpha");
        final var newEvent = TestEvents.event("alpha");
        when(service.create(request)).thenReturn(newEvent);
        mockMvc.perform(post(EventsApi.PATH_CREATE_EVENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("alpha"));
        verify(service).create(request);
    }

    @DisplayName("Should get event by id When the event exists")
    @Test
    void shouldGetEventByIdWhenTheEventExists() throws Exception {
        final var existingEvent = TestEvents.event("beta");
        when(service.get(existingEvent.getId()))
                .thenReturn(Optional.of(existingEvent));
        mockMvc.perform(get(EventsApi.PATH_GET_EVENT, existingEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingEvent.getId().toString()))
                .andExpect(jsonPath("$.title").value("beta"));
        verify(service).get(existingEvent.getId());
    }

    @DisplayName("Should return 404 When fetching an event that does not exist")
    @Test
    void shouldReturn404WhenFetchingAnEventThatDoesNotExist() throws Exception {
        when(service.get(any()))
                .thenReturn(Optional.empty());
        mockMvc.perform(get(EventsApi.PATH_GET_EVENT, UUID.randomUUID()))
                .andExpect(status().isNotFound());
        verify(service).get(any(UUID.class));
    }

    @DisplayName("Should replace event state When replacing an existing event")
    @Test
    void shouldReplaceEventStateWhenReplacingAnExistingEvent() throws Exception {
        final var id = UUID.randomUUID();
        final var upsertRequest = TestEvents.upsertRequest("gamma");
        upsertRequest.severity(Severity.WARN);
        final var latestEvent = TestEvents.event(id, "gamma");
        when(service.replace(id, upsertRequest)).thenReturn(latestEvent);
        mockMvc.perform(put(EventsApi.PATH_REPLACE_EVENT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("gamma"));
        verify(service).replace(id, upsertRequest);
    }

    @DisplayName("Should update event fields When patching an event")
    @Test
    void shouldUpdateEventFieldsWhenPatchingAnEvent() throws Exception {
        final var id = UUID.randomUUID();
        final var patchRequest = TestEvents.patchTitle("delta");
        final var latestEvent = TestEvents.event(id, "delta");
        when(service.patch(id, patchRequest)).thenReturn(latestEvent);
        mockMvc.perform(patch(EventsApi.PATH_PATCH_EVENT, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("delta"));
        verify(service).patch(id, patchRequest);
    }

    @DisplayName("Should delete event When deleting an event")
    @Test
    void shouldDeleteEventWhenDeletingAnEvent() throws Exception {
        final var id = UUID.randomUUID();
        mockMvc.perform(delete(EventsApi.PATH_DELETE_EVENT, id))
                .andExpect(status().isNoContent());
        verify(service).delete(id);
    }

}