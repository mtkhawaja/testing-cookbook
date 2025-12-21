package com.muneebkhawaja.testing.cookbook.autoconfiguration;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "checksum.service")
public record ChecksumProperties(
        boolean enabled
) {

    public ChecksumProperties() {
        this(true);
    }

}