package com.muneebkhawaja.testing.cookbook.containers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class CallbackConfiguration {
    @Bean
    BeforeConvertCallback<Event> beforeConvertCallback() {
        return (event) -> {
            if (event.getUuid() == null) {
                event.setUuid(UUID.randomUUID());
            }
            return event;
        };
    }
}
