package com.muneebkhawaja.testing.cookbook.containers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventStore {
    private final EventRepository repository;

    @Transactional
    public EventDTO save(final EventDTO dto) {
        final var entity = toEntity(dto);
        final var saved = repository.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<EventDTO> fetch(final UUID id) {
        return repository.findById(id).map(EventStore::toDto);
    }

    @Transactional
    public EventDTO update(final EventDTO dto) {
        if (dto.uuid() == null) {
            throw new IllegalArgumentException("Event 'uuid' cannot be null");
        }
        if (fetch(dto.uuid()).isEmpty()) {
            throw new IllegalArgumentException("Event with id '%s' does not exist".formatted(dto.uuid()));
        }
        return save(dto);
    }

    @Transactional
    public void delete(final UUID id) {
        if (id == null) {
            return;
        }
        repository.deleteById(id);
    }

    private static EventDTO toDto(Event entity) {
        return new EventDTO(entity.getUuid(), entity.getDescription());
    }

    private static Event toEntity(final EventDTO dto) {
        return new Event(dto.uuid(), dto.description());
    }
}
