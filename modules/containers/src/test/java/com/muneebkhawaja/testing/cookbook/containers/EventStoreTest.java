package com.muneebkhawaja.testing.cookbook.containers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
class EventStoreTest {
    @Autowired
    private EventRepository repository;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:18.0");
    @Autowired
    private EventStore service;

    @DisplayName("Should read event When the event exists")
    @Test
    void shouldReadEventWhenTheEventExists() {
        final var preExisting = repository.save(new Event(null, "pre-existing event"));
        assertThat(service.fetch(preExisting.getUuid())).isPresent();
    }

    @DisplayName("Should create event When creating a new event")
    @Test
    void shouldCreateEventWhenCreatingANewEvent() {
        final var dto = service.save(new EventDTO(null, "new event"));
        assertThat(repository.findById(dto.uuid())).isPresent();
    }

    @DisplayName("Should update event When updating an existing event")
    @Test
    void shouldUpdateEventWhenUpdatingAnExistingEvent() {
        final var v1 = repository.save(new Event(null, "v1"));
        service.update(new EventDTO(v1.getUuid(), "v2"));
        assertThat(this.repository.findById(v1.getUuid()))
                .isPresent()
                .get()
                .extracting(Event::getDescription)
                .isEqualTo("v2");
    }

    @DisplayName("Should raise exception event When updating a nonexistent event")
    @ParameterizedTest
    @NullSource
    @MethodSource("randomUUIDs")
    void shouldRaiseExceptionEventWhenUpdatingANonexistentEvent(UUID id) {
        assertThatThrownBy(() -> service.update(new EventDTO(id, "who cares")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<UUID> randomUUIDs() {
        return Stream.of(UUID.randomUUID());
    }


    @DisplayName("Should delete event When deleting an existing event")
    @Test
    void shouldDeleteEventWhenDeletingAnExistingEvent() {
        final var preExisting = repository.save(new Event(null, "will be deleted"));
        assertThat(service.fetch(preExisting.getUuid())).isPresent();
        service.delete(preExisting.getUuid());
        assertThat(service.fetch(preExisting.getUuid())).isEmpty();
    }

    @DisplayName("Should do nothing When deleting a nonexistent event")
    @ParameterizedTest
    @NullSource
    @MethodSource("randomUUIDs")
    void shouldDoNothingWhenDeletingANonexistentEvent(final UUID id) {
        assertThatNoException()
                .isThrownBy(() -> service.delete(id));
    }


}