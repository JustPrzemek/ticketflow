# ADR-0013: Purchase limits enforced per account and card fingerprint

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Organizers set a per-event purchase limit (default 1). Users could bypass it with multiple
accounts. Requiring phone numbers or ID documents adds friction and collects personal data we do not
want to store.

## Decision

The limit is enforced on two keys: the **user account** and the **Stripe card fingerprint** (a
stable identifier per card number that does not expose the number). A purchase is rejected if either
key has reached the limit for the event. Verified email is required to buy. Risk signals
(see Risk context) complement this.

## Consequences

- No additional personal data collected.
- Multiple accounts paying with the same card are capped.
- Users with several cards can still bypass the limit — accepted risk, mitigated by risk scoring.
- The fingerprint is known only after card details are entered, so the final check happens in the
  payment step, not at hold time.
