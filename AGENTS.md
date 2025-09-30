# Repository Guidelines

## Project Structure & Module Organization

The codebase is organized around the BadTourn domain.

- The application shell lives in `application/` with:
  - `organizer/` flows for staff
  - `audience/` read-only screens
- Core service logic sits under `service/tournamentmgmt`, a Spring Boot Modulith service exposing API controllers, configuration domain objects, and integration boundaries.
- Shared adapters reside in `3rd_party/` (authentication, event bus, search).
- Ops scripts and infra templates stay in `admin/`.
- Generated artifacts and local build outputs belong in `build/`.
- The GitHub wiki is vendored as the `wiki/` submodule; keep architectural notes there.

## Build, Test, and Development Commands

- Use Maven wrappers inside `service/tournamentmgmt/`.
  - Run `./mvnw clean verify` for a full compile, test, and packaging cycle.
  - Use `./mvnw test` during iterative development.
  - Launch the service with `./mvnw spring-boot:run` to expose REST endpoints and Modulith actuators.
- From the repo root, `./gradlew tasks` remains available for legacy modules but is optional today.
- Keep the wiki fresh with `git submodule update --remote --merge`.

## Coding Style & Naming Conventions

- Follow `.editorconfig`:
  - UTF-8 text
  - LF endings
  - Final newline
  - Max line length 120 for source
  - Two-space indentation across Java, Kotlin, Gradle, YAML, and JSON
- Prefer descriptive CamelCase for classes (e.g., `ConfigurationServiceImpl`), lowerCamelCase for members, and hyphenated resource paths.
- Group packages by bounded context such as `setup.configuration.api`.
- Use Lombok sparingly and annotate public APIs with Spring Modulith stereotypes when slicing modules.

## Testing Guidelines

- JUnit 5 with `spring-boot-starter-test` and `spring-modulith-starter-test` is the default.
- Place specs under `src/test/java` mirroring the main package.
- Name integration suites `*Tests`, reserving `*IT` for heavier scenarios if added later.
- Cover new endpoints and configuration flows with Modulith slice tests and controller MVC tests.
- Configure overrides via `src/test/resources` properties.
- Ensure the suite is green with `./mvnw test` before opening a PR.

## Commit & Pull Request Guidelines

- Commits follow Conventional Commit prefixes (`feat`, `chore`, `fix`); scope optional but encouraged.
- Start messages in the imperative and keep them under 72 characters when possible.
- For pull requests:
  - Include a summary of changes
  - Link any tracking issue
  - Outline test evidence
  - Attach UI/API artifacts (screenshots or curl samples) when behavior shifts
  - Note any wiki updates or migration steps so release managers can coordinate deployments.
