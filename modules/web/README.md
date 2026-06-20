# Web

HTTP-layer testing for an OpenAPI-generated **Events** CRUD API (`EventController implements EventsApi`).
The controller, models, and `PATH_*` constants are generated from `src/main/resources/open-api-spec.yaml`
by the `openapi-generator-maven-plugin`.

## Test variants

Server-side (drive the real controller):

- `EventControllerTest` / `EventControllerWebMVCTest` / `EventControllerManualWebMVCTest` — `@WebMvcTest`
  + MockMvc slices.
- `EventControllerRestClientTest` — full server (`RANDOM_PORT`) via `RestTestClient`.
- `EventControllerRestTemplateTest` — full server via `TestRestTemplate`.

Client-side (no real controller):

- `EventApiWireMockTest` — **WireMock** stubs the Events server; a `RestClient` calls it. Exercises
  client request building, response/error handling, and verifies the outgoing request. Gotcha: the
  client is pinned to HTTP/1.1 because the JDK client's default HTTP/2 trips WireMock's
  `RST_STREAM: Stream cancelled` on requests with a body.

Architecture:

- `ArchitectureTest` — ArchUnit fitness rules over this module's hand-written classes (the generated
  `com.muneebkhawaja.web.generated` API is excluded): `@RestController`s live in `..web..` and are
  named `*Controller`, no field injection, no `System.out`, no generic exceptions.

## Run

```bash
./mvnw test -pl modules/web
./mvnw test -pl modules/web -Dtest=EventApiWireMockTest   # one class
```
