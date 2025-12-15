package com.muneebkhawaja.testing.cookbook.web;

import com.muneebkhawaja.web.generated.api.EventsApi;
import com.muneebkhawaja.web.generated.model.Event;
import com.muneebkhawaja.web.generated.model.EventPatchRequest;
import com.muneebkhawaja.web.generated.model.EventUpsertRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class EventController implements EventsApi {

    private final EventService service;

    @Autowired
    public EventController(final EventService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Event> createEvent(final EventUpsertRequest request) {
        final var created = service.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Override
    public ResponseEntity<Void> deleteEvent(final UUID eventId) {
        service.delete(eventId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Event> getEvent(final UUID eventId) {
        return service.get(eventId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<Event> patchEvent(final UUID eventId, final EventPatchRequest request) {
        final var updated = service.patch(eventId, request);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Event> replaceEvent(final UUID eventId, final EventUpsertRequest request) {
        final var replaced = service.replace(eventId, request);
        return ResponseEntity.ok(replaced);
    }
}
