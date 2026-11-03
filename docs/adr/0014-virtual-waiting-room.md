# ADR-0014: Virtual waiting room for every on-sale

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Demand for an event is unpredictable. Without admission control, a spike overloads inventory and
payment services and degrades the experience for everyone.

## Decision

Every on-sale goes through a virtual waiting room. Users receive a queue position and are admitted at
a configurable rate (users per second) that matches system capacity. When load is low, admission is
immediate and the room is invisible to users. Admission is represented by a short-lived signed
token required by the Booking service.

## Consequences

- Protects downstream services and provides fair, first-come-first-served access.
- Adds a component on the critical path that itself must scale (backed by Redis).
