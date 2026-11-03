# ADR-0012: Stripe Connect for marketplace payments

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Customers pay for tickets; money belongs to organizers; the platform takes a fee. Refunds must be
supported, including mass refunds. The project must not handle card data.

## Decision

We will use **Stripe Connect** in test mode: organizers onboard connected accounts, customers pay via
PaymentIntents, the platform fee is applied per payment, and refunds go through the Stripe API.
Payment state changes arrive via signed webhooks. All Stripe calls use idempotency keys.
The provider sits behind a `PaymentGateway` port with a fake adapter for tests and load tests.

## Alternatives considered

- **PayU / Przelewy24** — popular locally; can be added later as additional adapters.

## Consequences

- Realistic marketplace payment flows (3-D Secure, webhooks, payouts) at zero cost.
- No PCI scope: card data never reaches Ticketflow.
- Load tests must use the fake adapter.
