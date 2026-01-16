# Auto Configuration Testing

## Purpose

This module shows how to test a custom Spring Boot auto-configuration class
using [ApplicationContextRunner](https://docs.spring.io/spring-boot//api/java/org/springframework/boot/test/context/runner/ApplicationContextRunner.html).

The example focuses on conditional bean creation and user overrides, which are the most common sources of
bugs in starter-style modules.

## Dependencies

- Spring Boot auto-configuration (`spring-boot-starter`)
- Spring Boot test utilities (`spring-boot-starter-test`)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Examples

- `ChecksumAutoConfigurationTest` validates conditional bean creation and override behavior:
  - Default behavior creates a `ChecksumService` when `checksum.service.enabled` is true.
  - Property-driven disablement avoids registering the bean.
  - User-provided bean overrides the auto-configured one.
- `ChecksumAutoConfiguration` + `MD5ChecksumService` is intentionally minimal so the tests stay focused on
  the auto-configuration mechanics rather than business logic.

## Main Tests

- `ChecksumAutoConfigurationTest` (ApplicationContextRunner coverage)
  - `src/test/java/com/muneebkhawaja/testing/cookbook/autoconfiguration/ChecksumAutoConfigurationTest.java`

## References

- [Creating Your Own Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)
