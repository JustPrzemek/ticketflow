# Event Storming — Notes

> Big-picture Event Storming, round 1 (2026-11-03).
> Domain events are written in past tense. This is a working draft: it will be refined in the
> design-level session and used to derive bounded contexts and the context map.

## Legend

| Sticky | Meaning |
|---|---|
| 🟧 Domain event | Something that happened (`TicketIssued`) |
| 🟦 Command | An intention (`Hold seats`) |
| 🟨 Actor | Who triggers the command |
| 🟪 Policy | "Whenever X happens, then do Y" |
| 🟥 Hot spot | Open question / risk |

## Timeline (draft)

### 1. Organizer onboarding
- 🟧 `OrganizerRegistered`
- 🟧 `PayoutAccountConnected` (Stripe Connect onboarding finished)

### 2. Event creation & moderation
- 🟧 `EventDrafted` → `VenueDefined` / `SeatingPlanDefined` / `TicketPoolDefined` → `PricingDefined`
- 🟧 `EventSubmittedForReview`
- 🟪 *Whenever an event is submitted → run automated pre-screening.*
- 🟧 `EventPreScreened` (score + reasons)
- 🟧 `EventApproved` / `EventRejected` (automatic for clear cases, otherwise by an admin)
- 🟧 `OnSaleScheduled`

### 3. On-sale & waiting room
- 🟧 `OnSaleOpened`
- 🟧 `CustomerJoinedQueue` → `CustomerAdmitted`
- 🟪 *Admit customers at the configured rate; immediately when load is low.*

### 4. Booking
- 🟧 `SeatsHeld` (10-minute expiry)
- 🟧 `HoldExpired` → seats released
- 🟧 `PurchaseLimitReached` (per account / per card fingerprint)
- 🟧 `OrderPlaced`

### 5. Payment
- 🟧 `PaymentInitiated` → `PaymentSucceeded` / `PaymentFailed`
- 🟪 *Whenever payment fails → release held seats.*
- 🟪 *Whenever payment succeeds → confirm order, issue tickets.*

### 6. Ticketing
- 🟧 `TicketsIssued` (signed QR) → `TicketPdfGenerated` → `TicketsDelivered`
- 🟧 `TicketScanned` → `TicketUsed` / `ScanRejected` (optional per event)

### 7. Refunds & cancellation
- 🟧 `RefundRequested` (customer, within refund window) → `RefundIssued` → `TicketRevoked` → seat returned to pool
- 🟧 `EventCancelled` (organizer)
- 🟪 *Whenever an event is cancelled → refund every ticket (mass refund).*

### 8. Payouts
- 🟧 `PayoutScheduled` → `PayoutCompleted`

### 9. Risk
- 🟧 `SuspiciousActivityDetected` → `PurchaseFlagged` / `AccountBlocked`

## Decisions from round 1

| Topic | Decision |
|---|---|
| Who creates events | Organizers (self-service marketplace); money goes to organizers |
| Moderation | Every listing is checked — automated pre-screening + admin review |
| Seating | Depends on the event type: numbered seats, ticket pools, or both — **to be designed** |
| Purchase limit | Per event, set by the organizer, default 1; enforced per account + card fingerprint |
| Hold time | 10 minutes |
| Delivery | PDF with signed QR code by email |
| Gate scanning | Optional per event; single use |
| Refunds | By customer (within window) and by organizer (event cancellation → mass refund); refunded tickets return to the pool |
| Resale | Not supported |
| Waiting room | For every on-sale |

## 🟥 Hot spots / open questions

1. **Event types and seating models** — which event types exist (concert hall, stadium, club with
   standing area, festival with multi-day passes, tables at a stand-up)? How are seating plans
   modelled and reused across events at the same venue?
2. **Ticket categories & pricing** — price tiers, early-bird, fees shown to the customer?
3. **Refund window** — who defines it and what are the bounds? Partial refunds? Service fee refunded?
4. **Organizer premium** — future: paid plan required to become an organizer.
5. **Payout timing** — after the event, or continuously? What happens to payouts if an event is cancelled?
6. **Event changes after on-sale** — date/venue change: notify customers, offer refunds?
7. **Moderation criteria** — what exactly is checked automatically vs. by an admin?
8. **Ticket transfer to a friend** — out of scope (no resale), but is a free name change allowed?
