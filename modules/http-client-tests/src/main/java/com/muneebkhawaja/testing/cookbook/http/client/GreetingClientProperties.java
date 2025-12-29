package com.muneebkhawaja.testing.cookbook.http.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "greeting.service")
@EnableConfigurationProperties(GreetingClientProperties.class)
public record GreetingClientProperties(URI baseUrl) {
}
