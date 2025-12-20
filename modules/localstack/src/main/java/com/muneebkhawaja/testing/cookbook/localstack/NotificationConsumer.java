package com.muneebkhawaja.testing.cookbook.localstack;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @SqsListener(queueNames = {"${notifications.object-repository.events.queue}"})
    public void consume(final ObjectRepositorySaveEvent event) {
        LOGGER.info("Consumed event notification: '{}'", event.assetReference().id());
    }
}
