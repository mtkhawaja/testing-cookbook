package com.muneebkhawaja.testing.cookbook.web;

import com.muneebkhawaja.web.generated.model.Event;
import com.muneebkhawaja.web.generated.model.EventPatchRequest;
import com.muneebkhawaja.web.generated.model.EventProperties;
import com.muneebkhawaja.web.generated.model.EventUpsertRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private final Map<UUID, Event> inMemoryStorage;

    public EventService() {
        this.inMemoryStorage = new ConcurrentHashMap<>();
    }

    public Event create(final EventUpsertRequest request) {
        final UUID id = UUID.randomUUID();
        final Event created = toEvent(id, request);
        inMemoryStorage.put(id, created);
        return created;
    }

    public Optional<Event> get(final UUID id) {
        return Optional.ofNullable(inMemoryStorage.get(id));
    }

    public void delete(final UUID id) {
        inMemoryStorage.remove(id);
    }

    public Event replace(final UUID id, final EventUpsertRequest request) {
        if (!inMemoryStorage.containsKey(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        final Event updated = toEvent(id, request);
        inMemoryStorage.put(id, updated);
        return updated;
    }

    public Event patch(final UUID id, final EventPatchRequest request) {
        final Event current = get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        if (request.getUpdateMask() == null || request.getUpdateMask().isEmpty()) {
            throw new IllegalArgumentException("updateMask must not be empty");
        }
        final EventProperties props = request.getEvent();
        if (props == null) {
            throw new IllegalArgumentException("event properties must be provided for patch");
        }
        final Event updated = copy(current);
        for (EventPatchRequest.UpdateMaskEnum field : request.getUpdateMask()) {
            update(field, updated, props);
        }
        inMemoryStorage.put(id, updated);
        return updated;
    }

    private static Event copy(Event current) {
        return new Event(current.getId())
                .title(current.getTitle())
                .severity(current.getSeverity())
                .timestamp(current.getTimestamp())
                .description(current.getDescription());
    }

    private static void update(final EventPatchRequest.UpdateMaskEnum field,
                               Event updated,
                               EventProperties props) {
        switch (field) {
            case TITLE -> updated.setTitle(props.getTitle());
            case SEVERITY -> updated.setSeverity(props.getSeverity());
            case TIMESTAMP -> updated.setTimestamp(props.getTimestamp());
            case DESCRIPTION -> updated.setDescription(props.getDescription());
            default -> throw new IllegalArgumentException("Unsupported patch field: " + field);
        }
    }

    private Event toEvent(@NonNull final UUID id, @NonNull final EventUpsertRequest request) {
        return new Event(id)
                .title(request.getTitle())
                .severity(request.getSeverity())
                .timestamp(request.getTimestamp())
                .description(request.getDescription());
    }


}
