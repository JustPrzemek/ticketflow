# ADR-0016: Scope of the first iteration (8 weeks)

- **Status:** Accepted
- **Date:** 2026-11-05

## Context

The full design (ADRs 0002–0015) is roughly 5–6 months of work. The first iteration has a budget of
about 8 weeks (~200 hours). The goal is to learn the core technologies and practices, not to ship
every business feature. Further features are added incrementally once the core works.

## Decision

The first iteration delivers the core of the architecture end-to-end, including deployment to
`qa` and `prod` in the cloud. The delivery pipeline is built early (week 2), so every later feature
flows through it.

**In scope:** Catalog, Booking, Payment, Ticketing (+ notifications, one service), Search, API gateway;
PostgreSQL (+ read replica), MongoDB, OpenSearch, Redis; Kafka with transactional outbox (polling
relay), idempotent consumers, choreographed saga, DLQ, JSON events; Stripe (test mode, plain
payments and refunds); numbered seats and ticket pools; admin approval of events; customer refunds
and mass refund on cancellation; signed QR tickets as PDF by email; per-account purchase limit;
Keycloak + Spring Security with token relay at the gateway; simplified waiting room, rate limiting,
Resilience4j; unit, Testcontainers, ArchUnit, API end-to-end and k6 load tests; OpenTelemetry with
an all-in-one Grafana stack; GitHub Actions, SonarQube Cloud, CodeRabbit, Renovate; k3d locally,
Helm, Argo CD; Oracle Cloud Always Free k3s hosting `qa` and `prod`; a minimal demo UI at the end.

**Deferred (stretch), partially superseding earlier ADRs:**

| Deferred item | Affects |
|---|---|
| Stripe Connect, organizer onboarding and payouts | ADR-0012 |
| Card-fingerprint purchase limit, bot / fraud detection | ADR-0013 |
| BFF pattern (gateway relays tokens instead) | ADR-0007 |
| Avro + Schema Registry (JSON first), Debezium CDC | ADR-0005, ADR-0008 |
| Gate scanning (tickets are signed, validation endpoint only) | ADR-0015 |
| Festival passes, table seating | Domain |
| AI moderation, semantic search, purchase assistant | — |
| Contract and mutation tests, chaos engineering | — |
| Terraform for cloud infrastructure, AWS EKS, in-cluster DB operators | ADR-0010 |
| Full Angular frontend | — |

## Consequences

- Every core pattern is still exercised; deferred items can be added without redesign because the
  boundaries stay the same.
- Each deferred item lives on the board under the *Stretch* milestone.
- Detailed stories are written just-in-time at the start of each week, not upfront.
