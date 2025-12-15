package com.muneebkhawaja.testing.cookbook.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muneebkhawaja.testing.cookbook.web.support.TestEvents;
import com.muneebkhawaja.web.generated.api.EventsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerManualWebMVCTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        EventController controller = new EventController(new EventService());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Should create event When creating a new event")
    @Test
    void shouldCreateEventWhenCreatingANewEvent() throws Exception {
        final var request = TestEvents.upsertRequest("alpha");
        final var severity = Objects.requireNonNull(request.getSeverity()).getValue();
        mockMvc.perform(post(EventsApi.PATH_CREATE_EVENT)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.severity").value(severity))
        ;
    }

}