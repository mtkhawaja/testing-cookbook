package com.muneebkhawaja.testing.cookbook.autoconfiguration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ChecksumAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ChecksumAutoConfiguration.class));

    @DisplayName("Should create ChecksumService bean when checksum.service.enabled is true (default)")
    @Test
    void shouldCreateChecksumServiceBeanWhenEnabledByDefault() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(ChecksumService.class));
    }

    @DisplayName("Should not create ChecksumService bean when checksum.service.enabled is false")
    @Test
    void shouldNotCreateChecksumServiceBeanWhenDisabled() {
        contextRunner
                .withPropertyValues("checksum.service.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ChecksumService.class));
    }

    @DisplayName("Should allow user to override ChecksumService bean")
    @Test
    void shouldAllowUserOverride() {
        contextRunner
                .withBean(ChecksumService.class, () -> input -> "user-override")
                .run(context -> {
                    assertThat(context).hasSingleBean(ChecksumService.class);
                    assertThat(context.getBean(ChecksumService.class).calculateChecksum("x".getBytes()))
                            .isEqualTo("user-override");
                });
    }

    @DisplayName("Should use MD5ChecksumService when no user override is provided")
    @Test
    void shouldUseMd5ChecksumServiceWhenNoOverrideProvided() {
        contextRunner.run(context -> {
            final var bean = context.getBean(ChecksumService.class);
            assertThat(bean).isInstanceOf(MD5ChecksumService.class);
        });
    }
}