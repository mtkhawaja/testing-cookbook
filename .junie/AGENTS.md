# Software Development Guidelines

Agents are expected to follow these guidelines when responding.

## General Principles

- Make small, reviewable changes. Implement one feature, fix, or refactor at a time; avoid large or sweeping rewrites
  unless explicitly requested.
- Follow existing patterns exactly. Analyze the codebase first and match its structure, naming, and design so new code
  looks native.
- Treat formatting and style as non-negotiable. Strictly follow editorconfig and project style rules; inconsistencies
  are unacceptable.
- Reuse proven internal examples. Prefer established, non-deprecated patterns already used in the repository.
- Use project-standard tooling only. Run the defined build, test, lint, and format commands (Maven/Gradle); no ad-hoc
  alternatives.
- Keep the build green. All tests must pass and no lint or format violations may be introduced.
- Tests must never be bypassed. Do not disable, skip, or comment out tests; fix the code or update the test with
  justification.
- Clarify ambiguity early. Ask for confirmation rather than implementing under uncertain assumptions.
- Communicate clearly and concisely. Describe what changed and why in neutral, professional language.
- Prefer the simplest correct solution. Avoid over-engineering; choose the clearest approach that solves the problem.
- Use clear, self-documenting names. Names must convey intent without relying on comments.
- Keep methods small and focused. Each method should do one thing; split methods that grow large or mix
  responsibilities.
- Default to immutability and purity. Prefer pure functions and avoid shared mutable state; isolate side effects at
  boundaries.
- Eliminate magic values. Replace hardcoded numbers or strings with named constants or enums.
- Instrument features with meaningful logging. Use the project’s logging framework, appropriate levels, and never log
  sensitive data or use `System.out/err`.
- Comment intent, not mechanics. Explain why non-obvious logic exists, not what the code already states.
- Handle exceptions deliberately. Catch specific exceptions, preserve causes, and never swallow errors.
- Reduce duplication and boilerplate. Apply DRY principles and refactor repeated logic into well-named utilities.
- Prefer the simplest suitable API. Use existing language or library features when they clearly express intent with less
  code.
- Avoid deeply nested code blocks. Prefer early returns, guard clauses, and small helper methods to keep control flow
  flat and readable. Flat code is easier to reason about, test, and modify than logic buried in multiple levels of
  indentation.

## Java Guidelines

- Use Standard Libraries: Don’t reinvent the wheel. Use Java’s standard library and well-adopted utilities.
- Prefer immutability by default. Declare variables, parameters, and fields as final unless the mutation is intentional
  and justified.
- Prefer constructor-based initialization over setter or field-based initialization and Require all mandatory
  dependencies via constructors.
- Do not omit braces for single-line if, for, while, or else statements.
- Use braces consistently to avoid ambiguity and accidental logic errors.

### Logging

- Logging must not affect control flow or program behavior.
- Use the project’s logging facade and defaults. Prefer SLF4J when available; otherwise use the project-standard
  logger (e.g., Log4j2). Do not introduce new logging frameworks.

```java
// slf4j example
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleClass.class);
}
```

```java
// log4j2 example
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SampleClass {
    private static final Logger LOGGER = LogManager.getLogger(SampleClass.class);
}
```

- Never use `System.out` or `System.err`. All logging must go through the configured logging framework.
- Use parameterized logging, not string concatenation. This avoids unnecessary object creation and ensures lazy
  evaluation.

```java
var obj = new Object();
LOGGER.info("Object created: {}", obj);     // Preferred
LOGGER.info("Object created: " + obj);      // AVOID
```

- Choose log levels intentionally.

* TRACE: Extremely detailed, low-level flow (rare; disabled by default).
* DEBUG: Diagnostic information useful during development or troubleshooting.
* INFO: High-level application events (startup, shutdown, major state changes).
* WARN: Unexpected situations that are recoverable or non-fatal.
* ERROR: Failures that impact functionality or require investigation.

- Do not log noise. Avoid logging inside tight loops, getters/setters, or obvious control flow unless it provides real
  diagnostic value.
- Log context, not just messages. Include identifiers (IDs, state, counts) that help diagnose issues, but avoid
  dumping entire objects unless necessary.
- Never log sensitive data. Do not log passwords, secrets, tokens, PII, or full request/response payloads unless
  explicitly sanitized and approved.
- Log once at the appropriate boundary. Avoid duplicate logging of the same exception at multiple layers; log where
  the error is best understood or handled.
- Preserve stack traces when logging errors. Always pass the exception as a parameter rather than logging e.getMessage()
  alone.

### Strings

- Prefer string literals or templates over concatenation. Avoid building strings with + when a single literal or
  formatted string is clearer.

```java
String msg = "Operation completed successfully";           // Preferred
String msg = "Operation " + "completed " + "successfully"; // Avoid
```

- Use formatting APIs for interpolation. Prefer String.format(...) or "%s".formatted(...) over manual concatenation when
  inserting values.

```java
String msg = "User %s logged in".formatted(username); // Preferred
String msg = "User " + username + " logged in";       // Avoid
```

- Use String.join for joining multiple strings. Prefer it over chained concatenation when combining known string
  elements.

```java
String path = String.join("/", "api", "v1", "users"); // Preferred
String path = "api" + "/" + "v1" + "/" + "users";     // Avoid
```

- Never concatenate strings in a loop. Use `StringBuilder` or `StringJoiner` to avoid unnecessary allocations.

```java
var sb = new StringBuilder();
for (var item : items) {
  sb.append(item);
}
```

### Exceptions

- Use exceptions for exceptional conditions. Do not use exceptions for normal control flow (e.g., breaking loops,
  expected “not found” cases).
- Catch the most specific exception type possible. Avoid catching Exception/Throwable unless you are at a true top-level
  boundary (e.g., a request handler) and can safely handle or translate failures.
- Never swallow exceptions. If you catch an exception, either handle it meaningfully, wrap and rethrow it with
  context, or translate it to the project’s error model.
- Preserve the cause chain. When wrapping, include the original exception as the cause to keep stack traces intact.
- Add context, not noise. Error messages should include actionable identifiers (IDs, operation names) but must not log
  or embed secrets/PII.
- Use domain-specific exceptions where it improves clarity. Prefer well-named custom exceptions (or error codes/results
  if that’s the project pattern) over generic runtime failures.
- Inspect interruption and cancellation. If catching InterruptedException, restore the interrupt flag (
  `Thread.currentThread().interrupt()`) and exit/propagate according to project conventions.
- Don’t hide failures in finally / cleanup. Cleanup must not mask the original exception; suppress secondary failures
  appropriately if needed.

### Resource Management

* Always use try-with-resources to ensure resources are closed when appropriate instead of `try-finally` blocks.

```java
try(final var reader = Files.newBufferedReader(Path.of(""))){}
```

### Validation

- Validate early and fail fast at boundaries. Treat all external input as untrusted and validate arguments, state, and
  invariants as soon as they enter the system to surface errors close to their source.
- Prefer `Objects.requireNonNull(...)` for simple null checks.
- Prefer blank checks for strings (null, empty, or whitespace). Use StringUtils.isBlank / isNotBlank when available.
- Fail with clear, specific errors. Validation failures should produce precise exceptions or error messages that clearly
  identify the invalid input without exposing sensitive data.
- Do not defer validation. Avoid allowing invalid data to propagate deeper into the system, where failures become harder
  to diagnose and recover from.

### Testing

- Use JUnit Jupiter by default (JUnit5 or above) unless the repository explicitly uses something else.
- Use AssertJ for assertions. Prefer AssertJ to JUnit assertions unless the project standard dictates otherwise.
- Every test must declare a method-level `@DisplayName`.
- Never use `@DisplayName` at the class level.
- `@DisplayName` text must be human-readable (natural language, spaces between words).
- The test method name must mirror the `@DisplayName` in camelCase.
- Test names must describe behavior, not implementation.
- Use behavior patterns: `shouldDoXWhenY`, `shouldNotDoXWhenYIsSomeCondition`, `shouldDoXWhenYGivenZ`.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {
    @DisplayName("Should save published event When the producer publishes a new event")
    @Test
    void shouldSavePublishedEventWhenTheProducerPublishesANewEvent() {...}
}
```

- Tests must be order-independent and parallel-safe. Do not share mutable state across tests, do not rely on execution
  order, and do not write tests that break under parallel execution.
- Keep tests narrowly scoped. Each test should verify one behavior/rule; avoid “wide” tests that assert multiple
  unrelated outcomes.
- Prefer parameterized tests for matrices and validation. Use @ParameterizedTest for input validation, edge cases, and
  scenario grids instead of duplicating test bodies.
- Tests must not be bypassed. Tests must not be skipped, disabled, or commented out to make the build pass.

#### Mockito Specific Guidelines

- Avoid brittle mocks. Do not write tests that require deep stubbing or mock-heavy setups tied to implementation
  details; prefer fakes, real collaborators, or higher-level tests when mocks become intrusive.
- Don't call `MockitoAnnotations.openMocks(...)` and don't create mocks imperatively
- Use JUnit Jupiter Mockito integration i.e., rely on `@ExtendWith(MockitoExtension.class)`
- Avoid global stubbings and stub interactions only inside the test method that depends on them.
- Prefer annotation-driven mocks (`@Mock`, `@Spy`, `@Captor`, `@InjectMocks`) e.g.

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    OrderRepository repository;
    @InjectMocks
    OrderService service;
}
```

- Use `@MockitoBean` when mocking Spring-managed beans and prefer `@MockitoBean` to `@MockBean` when both are available
  e.g.

```java
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentControllerTest {
    @MockitoBean
    PaymentService paymentService;
}
```

### Time Related

- Use UTC internally. Store and compute time as Instant (preferred) or OffsetDateTime with offset +00:00; convert to a
  user ZoneId only at presentation boundaries.
- Inject a Clock for time-sensitive code. Any code that calls “now” must use an injected java.time.Clock (not
  Instant.now() / OffsetDateTime.now() directly), so the behavior is deterministic and testable.
- Default the Clock to UTC. Use Clock.systemUTC() in production wiring; tests must use Clock.fixed(...) or
  Clock.offset(...) to control time.
- Never persist local time without offset. Avoid LocalDateTime for persisted/exchanged timestamps; it is ambiguous
  across time zones and DST.
- Be explicit about time zones. Never rely on the system default zone; always use explicit ZoneId when converting for
  display or user-specific behavior.
- Default to ISO 8601 for date/time formats. If no format is explicitly specified, use ISO 8601 (e.g.,
  2025-03-08T14:30:00Z) for consistency, interoperability, and unambiguous parsing.

### JShell

- JShell is available on Java 9+ only.
- Use JShell for quick experimentation and validation.
- JShell may be used to rapidly evaluate small Java snippets, APIs, or language features during exploration or
  debugging, but any validated logic must be translated into proper source code and tests—JShell output itself is not
  production code.

```shell
#!/usr/bin/env bash
echo 'var sum = 1 + 2; System.out.println(sum);' | jshell
```

### Comments

- Comment intent, not mechanics. Explain why the code exists or why a decision was made; do not restate what the code
  already makes obvious.
- Document public, user-facing APIs. Include concise descriptions, usage examples, and expected behavior for public
  classes, methods, or modules.
- Document exception scenarios explicitly. Clearly state which exceptions may be thrown, under what conditions, and
  whether callers are expected to handle them.
- Call out edge cases and invariants. Document non-obvious constraints, assumptions, limits, or invariants that are
  critical to correct usage.
- Avoid redundant or stale comments. Do not add comments that merely paraphrase code or are likely to become incorrect
  as the code evolves.
- Prefer documentation to inline comments when appropriate. Use Javadoc (or Markdown Javadoc where supported) for
  API-level intent; reserve inline comments for localized, non-obvious logic.
- Keep comments accurate and minimal. Fewer high-quality comments are preferred over many low-value ones.
- Prefer Markdown comments [JEP 467](https://openjdk.org/jeps/467), (Java 23+): Prefer Markdown for new documentation
  but do not alter existing documentation if standard javadoc is used.

```java
/// Represents a monetary amount in a specific currency.
///
/// ## Invariants
/// - Amount is non-negative
/// - Currency is ISO-4217 compliant
///
/// ## Example
/// ```java
/// Money price = new Money(BigDecimal.valueOf(19.99), Currency.getInstance("USD"));
/// ```
///
/// ## Notes
/// - This type is immutable
/// - Arithmetic operations return new instances
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
    }
}
```

### Java Version Specific Guidelines

* All language and library features up to and including the project’s configured Java version are supported.

- For Maven projects, determine the Java version from the maven-compiler-plugin (source / target) or the `java.version`
  property.
- For Gradle projects, determine the Java version from the Java toolchain or sourceCompatibility / targetCompatibility.
- If the Java version cannot be determined, assume the lowest LTS explicitly mentioned by the project; otherwise default
  to Java 8.

* Do not use features beyond that version unless explicitly approved. Key highlights of each version are given below:

#### Java 8 (LTS)

- Lambdas & Streams: Use for simple, readable transformations; avoid deeply nested or stateful pipelines.

```java

names.stream()
  .filter(User::isActive)
  .map(User::id)
  .collect(Collectors.toList()); // replace Collectors.toList() with toList if on Java 16+
```

- Optional: Use Optional only as a return type in public APIs. Optional is intended to model the possible absence of a
  return value and must never be used for fields, instance variables, method parameters, or collections (e.g.
  `List<Optional<T>>`).

```java
Optional<User> findUserById(Id id);
```

- java.time: Always prefer Instant, OffsetDateTime, or ZonedDateTime over Date/Calendar.

```java
Instant now = Instant.now(clock);
```

#### Java 11 (LTS)

- Use the standard HTTP client by default. Prefer java.net.http.HttpClient for HTTP calls unless the project already
  standardizes on a third-party client (e.g., Spring RestTemplate/RestClient/WebClient, Apache HttpClient); do not
  introduce a new client arbitrarily.

```java
client.send(request, BodyHandlers.ofString());
```

- String / Files helpers: Use isBlank, lines, readString, writeString etc. to reduce boilerplate.

```java
if (input.isBlank()) { ... }
Path filePath = Files.writeString(Files.createTempFile(tempDir, "demo", ".txt"), "Sample text");
String fileContent = Files.readString(filePath);
```

#### Java 17 (LTS)

- Records: Prefer for immutable data carriers; default choice for DTOs and value objects.

```java
public record OrderId(String value) {}

```

- Sealed types: Use for closed hierarchies (states, commands, ASTs).

```java
public sealed interface State permits Open, Closed {}
```

- Pattern matching (instanceof): Always prefer over manual casts.

```java
if (obj instanceof String s) { ... }
```

- Text blocks: Use for multi-line literals; never use multi line concatenation when available.

```java
String json = """
{ "id": 1, "active": true }
""";
```

- See [JEPs in JDK 17 integrated since JDK 11](https://openjdk.org/projects/jdk/17/jeps-since-jdk-11) for more details.

#### Java 21 (LTS)

- Virtual threads: Prefer for high concurrency, blocking workloads.

```java
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) { ... }
```

- Structured concurrency: Use for scoped, coordinated task lifecycles.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) { ... }
```

- Pattern matching for switch: Replace complex if/else chains.

```java
return switch (obj) {
        case String s -> s.length();
  case Integer i -> i;
default -> 0;
        };
```

- See [JEPs in JDK 21 integrated since JDK 17](https://openjdk.org/projects/jdk/21/jeps-since-jdk-17) for more details.