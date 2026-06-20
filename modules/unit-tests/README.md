# Unit Tests

Plain JUnit Jupiter unit-testing techniques — no Spring context, no Docker. Each sub-package is one
self-contained technique with its subject under `src/main/java` and the test under `src/test/java`.

## Examples

### `clock` — testing clock-based code

`AccessTokenFactory` takes an injected `java.time.Clock` instead of calling `Instant.now()`, so "now"
is deterministic. Tests pin time with `Clock.fixed(...)` and advance it with `Clock.offset(...)`.

> Trade-off: injecting a `Clock` is the only way to make time assertions deterministic — avoid
> `Instant.now()` / `LocalDateTime.now()` in code you want to test.

### `logging` — testing logging / capturing stdout

`AccountActivityLogger` logs through SLF4J. Two ways to verify it:

- **`AccountActivityLoggerTest`** attaches a Logback `ListAppender` to the logger and asserts on the
  captured events (level, formatted message, arguments). Robust — no text parsing.
- **`AccountActivityLoggerOutputCaptureTest`** uses Spring Boot's `OutputCaptureExtension` with a
  `CapturedOutput` parameter to assert on the rendered console text.

> Trade-off: prefer `ListAppender` for asserting on *log events*; reach for `OutputCaptureExtension`
> only when you need the raw stdout/stderr (e.g. third-party code that prints).

### `mockito` — annotation-driven Mockito

`OrderServiceTest` uses `@ExtendWith(MockitoExtension.class)` with `@Mock` collaborators,
`@InjectMocks` subject, and `@Captor` for argument capture. No `MockitoAnnotations.openMocks(...)`,
no deep stubs; strict stubbing is on by default.

### `extension` — a custom JUnit Jupiter extension

`SeededRandomExtension` implements `BeforeEachCallback` (seeds a deterministic `Random` into the
per-test `Store`) and `ParameterResolver` (injects an `int` into parameters annotated `@RandomInt`).
Shows the two extension points most custom extensions need, plus `Namespace`/`Store` usage.

### `spy` — partial mocks with Mockito spies

A spy wraps a **real** object: methods run for real unless you stub them.

- `SpyCollaboratorTest` — the recommended use: spy a real, trustworthy collaborator (`EventAuditor`)
  so you keep its real behaviour *and* can `verify(...)` the interaction. A plain `@Mock` would report
  `auditedCount() == 0`; the spy runs the real method and reports `1`.
- `PartialStubSpyTest` — stub one part of a real object (`normalize`) and let the rest run; plus the
  footgun: `when(spy.method())` calls the real method first (use `doReturn(...).when(spy).method()`).

> When spies make more sense than mocks:
> - you want the object's **real behaviour** but also need to verify interactions, or
> - you can only override **one part** of a real object — e.g. an expensive, non-deterministic, or
>   legacy/third-party method you can't easily inject around.
>
> Default to mocks, real collaborators, or fakes; reach for spies sparingly. For your *own* code,
> prefer a seam (composition) over partially stubbing a class's own methods — that's usually a design
> smell. And remember the stubbing gotcha: `doReturn(...).when(spy)`, not `when(spy....)`.

### `statics` — testing static methods (and a footgun)

Four techniques, simplest first:

1. **Call it directly** (`SlugsTest`) — a pure static utility is a pure function; just call it.
2. **`mockStatic`** (`ReferenceGeneratorStaticMockTest`) — when code calls a `static` you can't change
   (JDK / third-party), stub it with Mockito's `mockStatic` in a tight try-with-resources scope.
3. **Add a seam** (`SeamReferenceGeneratorTest`) — the preferred fix when you own the code: inject the
   dependency (e.g. `Supplier<UUID>`) so no static mocking is needed at all.
4. **The static-init footgun** (`StaticInitializationTest`) — a failing `static` initializer throws
   `ExceptionInInitializerError` on the *first* access and then `NoClassDefFoundError: Could not
   initialize class ...` on *every* later access, hiding the original cause. The test reproduces both
   by loading the class in a fresh classloader.

> Takeaways: keep `mockStatic` scopes tight (it intercepts *all* statics of that class on the thread);
> prefer a seam over static mocking; and keep heavy/failure-prone logic out of static initializers —
> it makes classes refuse to load with a cryptic error, especially under test.

### `instancio` — auto-generating test data

`AgePolicyInstancioTest` uses [Instancio](https://www.instancio.org/) to populate `Customer` objects:
`Instancio.create(...)` fills the whole object, `set(...)` / `generate(...)` with `field(...)`
selectors pin only the field under test, `ofList(...)` builds collections, and `withSeed(...)` makes
the data reproducible.

> Trade-off: let Instancio fill the fields a test doesn't care about so each test states only what
> matters; pin the relevant field with a selector. Use a seed when you need reproducible data.

## Run

```bash
./mvnw test -pl modules/unit-tests
```
