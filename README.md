# Ticketflow

An event-ticketing marketplace built as a distributed, event-driven system.
Organizers publish events, every listing goes through moderation, and customers buy tickets
during on-sales that can draw sudden spikes of traffic. Payments go to the organizers.

> **Status:** architecture & domain design (Phase 0 — not yet runnable).

## Key capabilities (target)

- Organizer self-service: venues, seating plans, ticket pools, pricing, refund policy
- Event moderation (automated pre-screening + admin review)
- Virtual waiting room for every on-sale, with controlled admission rate
- Seat holds with a 10-minute expiry, per-event purchase limits
- Marketplace payments with payouts to organizers
- Cryptographically signed QR tickets delivered as PDF, optional gate scanning
- Customer refunds and mass refunds when an event is cancelled
- Full-text and semantic event search
- Bot and fraud detection

## Architecture at a glance

| Concern | Choice |
|---|---|
| Language / framework | Java 25, Spring Boot 4 |
| Build | Maven (multi-module monorepo) |
| Messaging | Apache Kafka, Avro + Schema Registry, transactional outbox |
| Data | PostgreSQL, MongoDB, OpenSearch, Redis (database per service) |
| Identity | Keycloak (OIDC), Spring Security resource servers, BFF at the gateway |
| Payments | Stripe Connect (test mode) |
| Runtime | Docker, Kubernetes (k3d locally, k3s in the cloud), Helm |
| Delivery | GitHub Actions (CI), Argo CD (GitOps CD), Terraform (IaC) |
| Observability | OpenTelemetry, Prometheus, Grafana, Loki, Tempo |
| Quality | JUnit 5, Testcontainers, ArchUnit, contract tests, k6, JaCoCo, SonarQube Cloud, Renovate |

## Documentation

- [Architecture (arc42)](docs/architecture/arc42.md)
- [Architecture Decision Records](docs/adr/README.md)
- [Domain — Event Storming notes](docs/domain/event-storming.md)
- [Domain — Seating model](docs/domain/seating-model.md)

## License

© 2026 Przemysław Kubas. Source-available under the
[PolyForm Noncommercial License 1.0.0](LICENSE.md) — free to read, fork and use for
personal, educational and other noncommercial purposes. Commercial use is not permitted.
