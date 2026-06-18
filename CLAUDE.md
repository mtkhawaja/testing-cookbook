# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A multi-module Maven project where **each module is a small, self-contained example of one Java/Spring Boot
testing technique**. The point of each module is to show the *minimal*, copy/paste-friendly way to do that
kind of test, with the trade-offs written up in the module's own `README.md`.

When adding or changing an example: keep it minimal, keep it focused on the one technique the module exists to
demonstrate, and update that module's `README.md`. Don't pull in cross-module abstractions — modules are meant
to stand alone.

Requires **Java 25** and **Maven 4**; use the bundled `./mvnw`.

## Commands

```bash
./mvnw clean install                       # build + test everything (this is what CI runs)
./mvnw test -pl modules/web                # test a single module
./mvnw install -pl modules/web -am         # build one module + its dependencies
./mvnw test -pl modules/web -Dtest=SomeTest          # single test class
./mvnw test -pl modules/web -Dtest=SomeTest#method   # single test method
```

## Per-module gotchas

- **`containers`, `localstack`, `embedded-kafka`** need **Docker running** (Testcontainers / LocalStack spin up
  real services). They are slow and will fail fast without a Docker daemon.
- **`testng-tests`** is the one non-JUnit module. Tests run through a TestNG suite
  (`src/test/resources/suites/all.xml`) wired into surefire, so `-Dtest=` does not select them the usual way —
  edit the suite XML to change what runs.
- **`concurrency`** uses [Fray](https://github.com/cmu-pasta/fray) for deterministic concurrency testing. The
  `prepare-fray` Maven goal instruments the tests, so run them through Maven, not the IDE's plain JUnit runner,
  or the schedule exploration won't apply.

## Architecture

- Maven 4 reactor using `<subprojects>` in the root `pom.xml`. Parent is `spring-boot-starter-parent` 4.0.1;
  the version is `${revision}` (`1.0.0-SNAPSHOT`) shared across all modules.
- The root pom is applied to every module: `commons-lang3`, `lombok`, `spring-boot-devtools`, JaCoCo coverage,
  and the Lombok + Spring configuration-processor annotation processors. Each module adds only the
  dependencies specific to the technique it demonstrates.
- Modules and what each demonstrates:
  - `auto-configuration` — testing custom auto-config / starters with `ApplicationContextRunner`
  - `bdd` — Cucumber + Spring, multiple `@CucumberContextConfiguration`
  - `concurrency` — deterministic concurrency testing with Fray
  - `containers` — integration tests against real services via Testcontainers
  - `embedded-db` — in-memory DB patterns (H2 / Derby / HSQLDB)
  - `embedded-kafka` — messaging tests with embedded Kafka
  - `http-client-tests` — testing `RestTemplate` / `RestClient` callers
  - `localstack` — AWS-style integration tests against LocalStack
  - `testng-tests` — TestNG + Spring integration
  - `web` — HTTP layer tests: `@WebMvcTest`, MockMvc, `RestTestClient`, `TestRestTemplate`

## Conventions

Java, testing, Maven, concurrency, and observability/logging conventions for this repo are enforced by the
**`java-skills` plugin** (its `java-development`, `java-testing`, `java-maven`, `java-concurrency`, and
`java-observability` skills). Follow those rather than re-deriving style here. Repo-specific reminder: never
disable, skip, or comment out a test to get the build green.
