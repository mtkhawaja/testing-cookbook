package com.muneebkhawaja.testing.cookbook.embedded.kafka;

public interface EventRepository {
    void save(QuackEvent event);
}
