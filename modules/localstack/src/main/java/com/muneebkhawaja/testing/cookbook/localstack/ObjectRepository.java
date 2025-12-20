package com.muneebkhawaja.testing.cookbook.localstack;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Repository
public final class ObjectRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRepository.class);
    private final S3Template s3;
    private final String bucket;
    private final String keyPrefix;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;


    @Autowired
    public ObjectRepository(final S3Template s3,
                            @Value("${object.repository.s3.bucket-name}") final String bucket,
                            @Value("${object.repository.s3.key-prefix}") final String keyPrefix,
                            final ApplicationEventPublisher publisher) {
        this.clock = Clock.systemUTC();
        this.s3 = s3;
        this.bucket = bucket;
        this.keyPrefix = keyPrefix;
        this.publisher = publisher;
    }

    public AssetReference save(final byte[] bytes, final String contentType, final String name) {
        final UUID id = UUID.randomUUID();
        final String uploadedAt = DateTimeFormatter.ISO_INSTANT.format(OffsetDateTime.now(this.clock));
        final String key = String.format("%s/%s-%s", keyPrefix, id, name);
        // According to the docs, MD5 must be base 64 encoded:
        // software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder.contentMD5
        // 'The Base64 encoded 128-bit MD5 digest of the message (without the headers) according to RFC 1864.'
        final var md5Digest = Base64.getEncoder().encodeToString(DigestUtils.md5Digest(bytes));
        final long sizeBytes = bytes.length;
        final var metadata = ObjectMetadata.builder()
                .contentType(contentType)
                .contentLength(sizeBytes)
                .contentMD5(md5Digest)
                .metadata("id", id.toString())
                .build();
        LOGGER.info("Uploading asset to S3 bucket '{}' with key '{}'", this.bucket, key);
        this.s3.upload(this.bucket, key, new ByteArrayInputStream(bytes), metadata);
        final var assetReference = new AssetReference(
                id,
                this.bucket,
                key,
                contentType,
                md5Digest,
                uploadedAt
        );
        LOGGER.info("Uploaded Asset; Publishing application-level asset save event: '{}'", assetReference);
        this.publisher.publishEvent(new ObjectRepositorySaveEvent(assetReference));
        return assetReference;
    }

}