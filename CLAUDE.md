# Ticketflow — Project Guide

Event-ticketing marketplace built as event-driven microservices. Learning project that mirrors
how large commercial systems are designed, built, tested and delivered.

## Where things are

- **Code & docs:** this repo (public) — `JustPrzemek/ticketflow`
- **Backlog:** private repo `JustPrzemek/ticketflow-board` (issues only) + private GitHub Project
  "Ticketflow". Use `gh issue ... -R JustPrzemek/ticketflow-board` and `gh project ...` to read/update.
- **Architecture:** `docs/architecture/arc42.md`
- **Decisions:** `docs/adr/` — read relevant ADRs before changing anything architectural
- **Domain:** `docs/domain/event-storming.md`

## Current phase

**Planning** — Event Storming (deep dive: event types & seating), context map, backlog for Phase 0–1.
Next: Phase 0 — walking skeleton.

## Working rules

- Work goes through the board: pick an issue → short-lived branch → PR → merge to `main`.
- Branch names: `<type>/<board-issue-number>-<short-slug>`, e.g. `feat/12-catalog-skeleton`.
- Commits follow Conventional Commits: `feat(booking): hold seats with 10-minute expiry`.
  Scopes = service or area names (`catalog`, `booking`, `infra`, `ci`, `docs`, …).
- PRs reference the task: `Closes JustPrzemek/ticketflow-board#<n>`.
- **No AI attribution** anywhere: no `Co-Authored-By` trailers, no "generated with" footers,
  no AI mentions in README/docs. The author is Przemysław Kubas.
- **Never commit secrets.** Use `.env` (gitignored) locally, GitHub/Kubernetes Secrets elsewhere.
- Repo docs and code are in English. Conversation with the owner is in Polish.
- Architectural change → new or superseding ADR in the same PR.

## Engineering conventions (target)

- Java 25, Spring Boot 4, Gradle Kotlin DSL, convention plugins in `build-logic`.
- Hexagonal architecture per service: `domain` (no framework deps) → `application` (use cases,
  ports) → `adapter` (web, persistence, messaging). Enforced with ArchUnit.
- Events: Avro + Schema Registry, backward compatible, transactional outbox, idempotent consumers.
- Tests: JUnit 5 + AssertJ (unit), Testcontainers (integration), contract tests, k6 (load).
- Images: multi-arch (amd64 + arm64).
