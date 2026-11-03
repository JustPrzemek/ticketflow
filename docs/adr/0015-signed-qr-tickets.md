# ADR-0015: Cryptographically signed QR tickets with optional gate validation

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Tickets are delivered as PDFs with QR codes. They must be impossible to forge, and organizers may
want to validate them at the gate — sometimes with poor connectivity.

## Decision

The QR code contains a compact payload (ticket ID, event ID, seat/category, validity) signed with an
asymmetric key (e.g. Ed25519). Any scanner holding the public key can verify authenticity offline.
For events with gate scanning enabled, the scanner also marks the ticket as `USED` online; a second
scan is rejected. Refunded or cancelled tickets are revoked.

## Consequences

- Forgery is infeasible without the private key; key rotation must be supported.
- Offline-only validation cannot detect duplicates — online check is required for single use.
- New role `GATE_STAFF` and a simple browser-based scanner.
