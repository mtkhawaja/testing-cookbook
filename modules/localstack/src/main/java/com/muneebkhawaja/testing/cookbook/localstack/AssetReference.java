package com.muneebkhawaja.testing.cookbook.localstack;

import java.util.UUID;

public record AssetReference(
        UUID id,
        String bucketName,
        String key,
        String contentType,
        String contentMD5,
        String uploadedAt
) {
}
