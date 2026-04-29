# Copilot instructions for `traverson4j`

## Build, test, and lint commands

Use the Gradle wrapper from the repository root.

```bash
./gradlew build
```

```bash
./gradlew test
```

Run a single test class:

```bash
./gradlew :traverson4j-core:test --tests "uk.co.autotrader.traverson.TraversonBuilderTest"
```

Run a single test method:

```bash
./gradlew :traverson4j-core:test --tests "uk.co.autotrader.traverson.TraversonBuilderTest.from_SetsUrl"
```

Lint/static analysis tasks are module-scoped:

```bash
./gradlew checkstyleMain checkstyleTest spotbugsMain spotbugsTest
```

Security dependency scan tasks:

```bash
./gradlew dependencyCheckAnalyze
```

## High-level architecture

This is a Java 21 multi-module library. Active modules are defined in `settings.gradle`:

- `traverson4j-core`: traversal API, request/response model, link discovery, and resource conversion
- `traverson4j-hc5`: Apache HttpComponents 5 implementation of `TraversonClient`
- `traverson4j-jackson2`: optional Jackson converter integration

Core traversal flow:

1. `Traverson` is a lightweight entry point intended for injection, and creates a new `TraversonBuilder` per call to `from(...)`.
2. `TraversonBuilder` is stateful (explicitly not thread-safe), accumulates request state, and performs traversal by issuing `GET` requests for each rel in `follow(...)` before executing the terminal HTTP method.
3. Link resolution strategy is selected by media type:
   - `json()` => `BasicLinkDiscoverer`
   - `jsonHal()` => `HalLinkDiscoverer` (delegates through HAL-specific handlers)
4. HTTP execution is delegated to `TraversonClient`; in this repo the default implementation is `ApacheHttpTraversonClientAdapter` in `traverson4j-hc5`.
5. Response body conversion is handled by `ResourceConversionService` (singleton in core), seeded with built-in converters and extended via Java `ServiceLoader`.

Extension points:

- Add new return-type mapping via `ResourceConverter` and `META-INF/services/uk.co.autotrader.traverson.conversion.ResourceConverter` (used by `traverson4j-jackson2`).
- Add new request body type by registering a converter in `traverson4j-hc5` `BodyFactory`.

## Key conventions in this codebase

- Keep `Traverson` reusable, but treat each `TraversonBuilder` as one request/traversal specification.
- `follow(...)` replaces the rel sequence (it clears previous rels before adding new ones).
- Query/template params are additive; headers overwrite by key.
- `build` enforces 100% per-class line and branch coverage via `jacocoTestCoverageVerification` (minimum `1.0` for both counters).
- Checkstyle config is centralized at `config/checkstyle/checkstyle.xml`; SpotBugs uses `exclude.xml`.
- CI uses Java 21 and runs `./gradlew build`; release publishing runs `./gradlew build publish jreleaserDeploy`.
- Public artifact usage from README: consumers typically depend on `traverson4j-hc5`, and add `traverson4j-jackson2` when Jackson object mapping is required.
