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

**Now:** week 2 — delivery pipeline (board epic #8, tasks #24–#29). Week 1 is closed.
Done: #24 — local k3d cluster and Helm chart for Catalog (`docs/local-kubernetes.md`).
Next: #26 (ADR: GitOps repository layout and secrets, incl. MongoDB credentials) and #25 (Oracle
Cloud VM with k3s), then #27 Argo CD.

## Session routine

- **Start:** read this file, check the board (`gh project item-list 1 --owner JustPrzemek`),
  `git log --oneline -10`, continue the item that is *In Progress*.
- **Review:** after implementing, walk the owner through the change in the IDE — one topic at a
  time, short messages, then the problems we hit. Understanding comes before the next feature.
- **End:** update "Now" above, comment progress / next step on the active board issue, commit.

## Commands

- Toolchain: `mise install` (JDK, Maven, k3d, Helm and kubectl pinned in `mise.toml`)
- Local infrastructure: `docker compose up -d`
- Build + all tests: `./mvnw verify` (integration tests `*IT` need Docker)
- Unit tests only: `./mvnw test`
- Coverage reports after `./mvnw verify`: `services/<service>/target/site/jacoco/index.html`
  (unit tests) and `.../jacoco-it/index.html` (integration tests)
- Run Catalog: `./mvnw -pl services/catalog-service spring-boot:run` (needs `docker compose up -d`)
  or `spring-boot:test-run` (starts MongoDB in a container itself) → http://localhost:8081/swagger-ui.html
- Sample requests: `services/<service>/http/*.http`
- Local Kubernetes: `k3d cluster create --config k3d/cluster.yaml`, then
  `helm upgrade --install catalog-service charts/catalog-service --wait`; delete with
  `k3d cluster delete ticketflow`. Guide: `docs/local-kubernetes.md`
- Check a chart: `helm lint --strict charts/<service>` and `helm template <release> charts/<service>`
- Container image (from repo root): `docker build -f services/<service>/Dockerfile -t ticketflow-<service> .`
  — CI pushes `ghcr.io/justprzemek/ticketflow-<service>:<sha>` (amd64 + arm64) on merge to `main`.

## Working rules

- Work goes through the board: pick an issue → short-lived branch → PR → merge to `main`.
- Branch names: `<type>/<board-issue-number>-<short-slug>`, e.g. `feat/12-catalog-skeleton`.
- Commits follow Conventional Commits: `feat(booking): hold seats with 10-minute expiry`.
  Scopes = service or area names (`catalog`, `booking`, `infra`, `ci`, `docs`, …).
- PRs reference the task: `Closes JustPrzemek/ticketflow-board#<n>`.
- **No AI attribution** anywhere: no `Co-Authored-By` trailers, no "generated with" footers,
  no AI mentions in README/docs. The author is Przemysław Kubas.
- **Never commit secrets.** Use `.env` (gitignored) locally, GitHub/Kubernetes Secrets elsewhere.
- Repo docs, code and board issues are in English. Conversation with the owner is in Polish.
- Architectural change → new or superseding ADR in the same PR.

## Engineering conventions (target)

- Java 25, Spring Boot 4, Maven multi-module (root POM = shared config), `./mvnw`; JDK pinned in `mise.toml`.
- Code style differs per service on purpose (ADR-0018): Catalog = strict hexagonal (no Spring in
  `domain`/`application`, wiring only in `config`), Booking = clean, Payment = onion,
  Ticketing = layered, Search = vertical slices. Each enforced with ArchUnit.
- Tests: fakes for stateful ports (repositories), Mockito for interactions (senders, publishers)
  and where a service has no ports to fake (layered Ticketing/Notification mock their repositories).
  Replace only our own interfaces, never library types — code using those is tested against real
  infrastructure.
- Events: Avro + Schema Registry, backward compatible, transactional outbox, idempotent consumers.
- Tests: JUnit 5 + AssertJ (unit), Testcontainers (integration), contract tests, k6 (load).
  Levels, doubles and naming conventions: `docs/architecture/arc42.md` §8.1.
- Images: multi-arch (amd64 + arm64).
