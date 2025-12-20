package com.muneebkhawaja.testing.cookbook.localstack;

import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(LocalStackS3AndSQSSupport.class)
public class SaveEventPublishingFlowTest {
    @Autowired
    private ObjectRepository objectRepository;
    @MockitoSpyBean
    private NotificationConsumer notificationConsumer;

    @DisplayName("Should publish notification to SQS When an asset is saved to S3")
    @Test
    void shouldPublishNotificationToSqsWhenAnAssetIsSavedToS3() {
        final var content = RandomStringUtils.secure().nextAlphabetic(100);
        final var bytes = content.getBytes(StandardCharsets.UTF_8);
        objectRepository.save(bytes, MediaType.TEXT_PLAIN_UTF_8.toString(), "example-file");
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        verify(notificationConsumer).consume(any(ObjectRepositorySaveEvent.class))
                );
    }

}
