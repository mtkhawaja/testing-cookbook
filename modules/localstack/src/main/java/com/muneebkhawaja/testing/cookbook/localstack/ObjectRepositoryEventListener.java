package com.muneebkhawaja.testing.cookbook.localstack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ObjectRepositoryEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRepositoryEventListener.class);
    private final NotificationPublisher notificationPublisher;

    @Autowired
    public ObjectRepositoryEventListener(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @EventListener
    public void handleSaveEvent(final ObjectRepositorySaveEvent event) {
        final var id = event.assetReference().id();
        LOGGER.info("Received application event: '{}'; Attempting publish to SQS", event.assetReference().id());
        this.notificationPublisher.publish(event);
    }
}
