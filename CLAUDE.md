# Ticketflow — Project Guide

Event-ticketing marketplace built as event-driven microservices. Learning project that mirrors
how large commercial systems are designed, built, tested and delivered.

## Where things are

- **Code & docs:** this repo (public) — `JustPrzemek/ticketflow`
- **Backlog:** private repo `JustPrzemek/ticketflow-board` (issues only) + private GitHub Project
  "Ticketflow" (#1, https://github.com/users/JustPrzemek/projects/1).
  Use `gh issue ... -R JustPrzemek/ticketflow-board` and `gh project ... --owner JustPrzemek`.
  Hierarchy: Epic (one per milestone/phase) → Story/Spike → Task, linked as sub-issues.
  Status: Backlog → Ready → In Progress → In Review → Done.
- **Architecture:** `docs/architecture/arc42.md`
- **Decisions:** `docs/adr/` — read relevant ADRs before changing anything architectural
- **Domain:** `docs/domain/event-storming.md`

## Current phase

First iteration (8 weeks) — scope in `docs/adr/0016-first-iteration-scope.md`.
Plan (one milestone per week on the board):

1. Walking skeleton — Maven monorepo, Catalog service, tests, CI, quality tools
2. Delivery pipeline — k3d, Helm, Argo CD, Oracle Cloud k3s with `qa` and `prod`
3. Booking & Kafka — seat holds in Redis, outbox, idempotent consumers
4. Payments, saga & tickets — Stripe test mode, compensations, DLQ, signed QR PDF
5. Identity & refunds — Keycloak, gateway, refunds, event cancellation
6. Search, read scaling & observability — OpenSearch CQRS, read replica, OpenTelemetry
7. Resilience & load — waiting room, rate limiting, Resilience4j, k6
8. Demo UI & buffer

**Now:** week 1 — walking skeleton (Catalog service, tests, CI).

## Session routine

- **Start:** read this file, check the board (`gh project item-list 1 --owner JustPrzemek`),
  `git log --oneline -10`, continue the item that is *In Progress*.
- **End:** update "Now" above, comment progress / next step on the active board issue, commit.

## Commands

- Toolchain: `mise install` (JDK + Maven pinned in `mise.toml`)
- Local infrastructure: `docker compose up -d`
- Build + all tests: `./mvnw verify` (integration tests `*IT` need Docker)
- Unit tests only: `./mvnw test`
- Run Catalog: `./mvnw -pl services/catalog-service spring-boot:run` (needs `docker compose up -d`)
  or `spring-boot:test-run` (starts MongoDB in a container itself) → http://localhost:8081/swagger-ui.html
- Sample requests: `services/<service>/http/*.http`

## Working rules

- Work goes through the board: pick an issue → short-lived branch → PR → merge to `main`.
- Branch names: `<type>/<board-issue-number>-<short-slug>`, e.g. `feat/12-catalog-skeleton`.
- Commits follow Conventional Commits: `feat(booking): hold seats with 10-minute expiry`.
  Scopes = service or area names (`catalog`, `booking`, `infra`, `ci`, `docs`, …).
- PRs reference the task: `Closes JustPrzemek/ticketflow-board#<n>`.
- **No AI attribution** anywhere: no `Co-Authored-By` trailers, no "generated with" footers,
  no AI mentions in README/docs. The author is Przemysław Kubas.
- **Never commit secrets.** Use `.env` (gitignored) locally, GitHub/Kubernetes Secrets elsewhere.
- Repo docs and code are in English. Board issues and conversation with the owner are in Polish.
- Architectural change → new or superseding ADR in the same PR.

## Engineering conventions (target)

- Java 25, Spring Boot 4, Maven multi-module (root POM = shared config), `./mvnw`; JDK pinned in `mise.toml`.
- Hexagonal architecture per service: `domain` (no framework deps) → `application` (use cases,
  ports) → `adapter` (web, persistence, messaging). Enforced with ArchUnit.
- Events: Avro + Schema Registry, backward compatible, transactional outbox, idempotent consumers.
- Tests: JUnit 5 + AssertJ (unit), Testcontainers (integration), contract tests, k6 (load).
- Images: multi-arch (amd64 + arm64).
