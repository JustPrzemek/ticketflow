# ADR-0002: Event-driven microservices aligned with bounded contexts

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

The domain contains areas with very different load profiles and consistency needs: a read-heavy
catalog, a write-contended inventory during on-sales, payments that require strict consistency,
and slow side effects (PDF generation, email). The project's goal is also to practice the
architecture used by large-scale commercial systems.

## Decision

We will build Ticketflow as microservices whose boundaries follow DDD bounded contexts discovered
through Event Storming. Services collaborate primarily through asynchronous domain events;
synchronous calls are used only where a user is waiting for an immediate answer.
Contexts may start inside a shared service and be split out when there is a concrete reason.

## Alternatives considered

- **Modular monolith** — simpler to operate and a sensible default for most products, but it would
  not exercise independent scaling, deployment and failure isolation, which are learning goals.
- **Synchronous REST-only microservices** — tight temporal coupling; a failure in one service
  cascades into others.

## Consequences

- Independent scaling (e.g. Booking during on-sales) and failure isolation.
- Eventual consistency between services must be designed for explicitly (sagas, compensations,
  idempotent consumers).
- Significant operational overhead: CI/CD, observability and local tooling are mandatory, not optional.
- Wrong boundaries are expensive — boundaries are validated before implementation.
