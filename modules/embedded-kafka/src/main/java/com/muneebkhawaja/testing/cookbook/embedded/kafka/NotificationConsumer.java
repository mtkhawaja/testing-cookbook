package com.muneebkhawaja.testing.cookbook.embedded.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EventRepository repository;


    @KafkaListener(topics = "quacks")
    public void listen(final QuackEvent event) {
        log.info("Received event: '{}'", event);
        this.repository.save(event);
    }


}
