# ArchUnit Tests

Architecture / fitness tests with [ArchUnit](https://www.archunit.org/): rules about *structure* that
run as ordinary unit tests and fail the build when the architecture drifts.

A tiny layered sample (`web` → `service` → `persistence`) is the thing under analysis;
`ArchitectureTest` enforces rules over it via `@AnalyzeClasses` + `@ArchTest`:

- **Layered access** — web → service → persistence only; no back-dependencies.
- **Naming** — classes in `..service..` end with `Service`.
- **Dependency direction** — web must not reach into persistence.
- **No package cycles** (`slices()`).
- **House coding rules** — no `System.out`/`System.err`, no throwing generic exceptions
  (`GeneralCodingRules`).

## Where it fits

ArchUnit analyzes *compiled classes*, so it lives in its own module with a sample to check. In a real
multi-module build you'd add an `ArchitectureTest` to each module (scanning that module's base package)
rather than one central module — a module can only see classes on its own classpath.

## Run

```bash
./mvnw test -pl modules/arch-unit
```
