# ADR-0009: CQRS read models and PostgreSQL read replicas

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Reads vastly outnumber writes. Two different read problems exist:
1. Search needs a data shape the write model does not have (full-text, facets, joined data from
   several contexts).
2. Some services serve many reads of the same shape as their write model (e.g. "my orders").

## Decision

- **CQRS** for search: the Search service builds an OpenSearch read model from domain events
  (catalog, availability, moderation). The model can be rebuilt by replaying events.
- **Read replicas** for Booking: PostgreSQL primary + streaming replica; read-only transactions
  are routed to the replica.

## Consequences

- Reads scale independently of writes.
- Read models and replicas are eventually consistent; the UI and APIs must tolerate lag
  (e.g. read-your-own-writes from the primary where required).
