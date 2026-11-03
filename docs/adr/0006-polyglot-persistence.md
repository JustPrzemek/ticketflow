# ADR-0006: Database per service with polyglot persistence

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Each bounded context has different data shapes and access patterns. Sharing a database couples
services at the schema level and defeats independent deployment.

## Decision

Each service owns its data; no other service reads or writes it directly. The store is chosen per
context:

| Store | Used for | Why |
|---|---|---|
| PostgreSQL | Booking, Payment, Ticketing, Organizer, Moderation | ACID transactions, constraints, strong consistency |
| MongoDB | Event Catalog | Flexible documents for very different event types and seating plans |
| OpenSearch | Search read model | Full-text search, filtering, aggregations |
| Redis | Seat holds, waiting room, cache, rate limiting, counters | In-memory speed, TTL, atomic operations |

## Alternatives considered

- **PostgreSQL for everything** (with JSONB and full-text search) — viable and simpler, but would
  not exercise trade-offs between store types, a learning goal of the project.

## Consequences

- Each store is used where it is strongest.
- No cross-service joins: data needed by another service is shared through events and
  replicated into that service's own model.
- More infrastructure to run, back up and monitor.
