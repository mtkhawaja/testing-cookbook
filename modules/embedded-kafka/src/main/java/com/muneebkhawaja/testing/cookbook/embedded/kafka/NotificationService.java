package com.muneebkhawaja.testing.cookbook.embedded.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {
    private final KafkaTemplate<UUID, QuackEvent> template;
    private final String topic;


    public NotificationService(
            final KafkaTemplate<UUID, QuackEvent> template,
            final @Value("${app.kafka.topic.quack:quacks}") String topic
    ) {
        this.template = template;
        this.topic = topic;
    }


    public void publish(final QuackEvent event) {
        this.template.send(this.topic, event.uuid(), event);
    }
}
