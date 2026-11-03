# ADR-0008: Transactional outbox for reliable event publishing

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

A service must update its database and publish an event as one logical operation. Writing to the
database and to Kafka separately (dual write) can leave them inconsistent if either step fails.

## Decision

Services write events to an `outbox` table in the same database transaction as the state change.
A relay publishes outbox records to Kafka. We start with a polling relay and move to change data
capture with Debezium once the basics work. Consumers deduplicate using the event ID.

## Alternatives considered

- **Dual write** — loses or invents events on partial failure.
- **Event sourcing** — powerful but a much larger paradigm shift; may be explored for a single
  aggregate later.

## Consequences

- At-least-once event delivery without distributed transactions.
- Requires idempotent consumers and outbox cleanup.
