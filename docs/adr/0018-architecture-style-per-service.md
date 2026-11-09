# ADR-0018: Code architecture style per service

- **Status:** Accepted
- **Date:** 2026-11-07

## Context

Inside each service the code can follow different structural styles. Hexagonal, onion and clean
architecture share the same core idea (a framework-free core, dependencies pointing inwards) and
differ mostly in naming. As a learning project, Ticketflow deliberately uses genuinely different
styles, each matched to the nature of the service — as happens in organizations where services are
owned by different teams.

## Decision

| Service | Style | Why it fits |
|---|---|---|
| Catalog | **Hexagonal (ports & adapters), strict** — no Spring in `domain`/`application`; wiring only in `config` | Clear core with REST and MongoDB at the edges |
| Booking | **Clean Architecture** — one class per use case, rich domain | Most complex business rules (holds, limits, concurrency) |
| Payment | **Onion** — explicit domain-services ring | Payment logic spanning several domain objects |
| Ticketing + Notification | **Layered** — controller → service → repository | Simple pipeline; shows where classic layering is enough |
| Search | **Vertical slices** — package per feature/query | Read-only model; queries are independent |

Each service enforces its own style with ArchUnit rules. A service's style is refined in a
follow-up ADR when the service is started.

## Consequences

- Direct comparison of styles on real code; trade-offs become visible.
- Onboarding cost: every service must be read with its own conventions — each service gets a short
  `README.md` describing its structure.
