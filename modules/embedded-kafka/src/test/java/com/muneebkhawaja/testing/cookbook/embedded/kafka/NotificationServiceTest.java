package com.muneebkhawaja.testing.cookbook.embedded.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TestApplication.class, properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
@EmbeddedKafka(kraft = true)
class NotificationServiceTest {
    @MockitoBean
    private EventRepository repository;
    @Autowired
    private NotificationService service;


    @DisplayName("Should save published event When the producer publishes a new event")
    @Test
    void shouldSavePublishedEventWhenTheProducerPublishesANewEvent() {
        final var event = new QuackEvent(UUID.randomUUID(), "Donald", "Mallard");
        service.publish(event);
        verify(repository, timeout(TimeUnit.MINUTES.toMillis(1))).save(event);

    }

}