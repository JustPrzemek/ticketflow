# ADR-0001: Record architecture decisions

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Architectural decisions are made continuously over months. Without a record, the reasoning behind
them is lost and decisions get re-litigated or silently violated.

## Decision

We will record every significant architectural decision as an Architecture Decision Record (ADR)
in `docs/adr/`, using [`template.md`](template.md). ADRs are immutable once accepted; a changed
decision is captured in a new ADR that supersedes the old one.

## Consequences

- The "why" of the architecture is versioned together with the code and reviewed in pull requests.
- Writing an ADR is part of the Definition of Done for any PR that changes the architecture.
