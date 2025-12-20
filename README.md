# Testing Cookbook

[![CI](https://github.com/mtkhawaja/testing-cookbook/actions/workflows/build.yaml/badge.svg?branch=main)](https://github.com/mtkhawaja/testing-cookbook/actions/workflows/build.yaml)
![Java 25](https://img.shields.io/badge/java-25-orange)
![Maven 4](https://img.shields.io/badge/maven-4-blue)

A collection of small, focused examples for common testing scenarios in Java and Spring Boot.

The goal: show a *minimal* implementation for each testing approach, including the trade-offs, and provide copy/paste-friendly starting points.

## Prerequisites

- Java 25 or above
- Maven 4.x
- Modern [Docker Installation](https://docs.docker.com/get-started/get-docker/)

Suggestion: Use [sdkman](https://sdkman.io/) to configure java and maven versions.

## Quickstart

```shell
#!/usr/bin/env bash

./mvnw clean install
```

## What’s inside

This repo is organized as a multi-project Maven project:

- [`modules/containers`](./modules/containers/README.md) — Integration tests with real services via Testcontainers
- [`modules/embedded-db`](./modules/embedded-db/README.md) — In-memory DB testing patterns (e.g., H2, Derby, HSQLDB)
- [`modules/embedded-kafka`](./modules/embedded-kafka/README.md) — Messaging tests with embedded Kafka
- [`modules/localstack`](./modules/localstack/README.md) — AWS integration-style tests locally with LocalStack and Test Containers
- [`modules/web`](./modules/web/README.md) — HTTP Layer tests via: Mockito, @WebMvcTest, RestTestClient, and TestRestTemplate.

## References

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/reference/testing/index.html)
