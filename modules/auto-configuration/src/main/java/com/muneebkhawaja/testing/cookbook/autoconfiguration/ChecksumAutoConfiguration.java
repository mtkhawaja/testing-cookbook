package com.muneebkhawaja.testing.cookbook.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(ChecksumProperties.class)
@ConditionalOnProperty(
        prefix = "checksum.service",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ChecksumAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ChecksumService hashingService(final ChecksumProperties ignoreForNow) {
        return new MD5ChecksumService();
    }
}