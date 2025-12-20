package com.muneebkhawaja.testing.cookbook.localstack;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);
    private final SqsTemplate template;
    private final String queue;

    @Autowired
    public NotificationPublisher(final SqsTemplate template,
                                 final @Value("${notifications.object-repository.events.queue}") String topicName) {

        this.template = template;
        this.queue = topicName;
    }

    public void publish(final ObjectRepositorySaveEvent event) {
        this.template.send(to -> to.queue(this.queue).payload(event));
        LOGGER.info("Published event notification to SQS: '{}'", event.assetReference().id());
    }
}
