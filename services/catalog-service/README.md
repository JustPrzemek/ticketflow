# Catalog Service

Owns **what exists** on the platform: events, their venues and (later) seating plans and pricing.
Organizers create events here; after moderation they are published to the rest of the system.

**Not responsible for:** seat availability and holds (Booking), payments (Payment),
issued tickets (Ticketing), full-text search (Search). Those services learn about events from
the `EventPublished` event — see `docs/domain/seating-model.md`.

## Current scope

| Endpoint | Description |
|---|---|
| `POST /api/events` | Create an event draft. Organizer from the `X-Organizer-Id` header (temporary, until Keycloak). Returns `201` + `Location`. |
| `GET /api/events/{id}` | Read an event. `404` as RFC 9457 problem details. |

Validation errors return `400 application/problem+json`. OpenAPI UI: `/swagger-ui.html`.

## Key decisions

| Decision | Why | ADR |
|---|---|---|
| **MongoDB** | An event with its venue and seating plan is one aggregate read and written as a whole; plans differ a lot between event types, so a flexible document fits better than many tables. | [0006](../../docs/adr/0006-polyglot-persistence.md) |
| **Hexagonal architecture, strict** | Business rules stay free of Spring and MongoDB, are tested in milliseconds and survive infrastructure changes. | [0018](../../docs/adr/0018-architecture-style-per-service.md) |
| **No Lombok, records instead** | Plain Java is enough; the core has no tool or framework dependencies. | [0018](../../docs/adr/0018-architecture-style-per-service.md) |
| **Testcontainers, no in-memory DB** | Integration tests run against the same MongoDB version as production. | — |

## Structure

```
com.ticketflow.catalog
├── domain.model          Event aggregate, value objects (EventId, Venue, …), invariants
├── application
│   ├── port.in           use cases: CreateEventDraftUseCase, GetEventUseCase
│   ├── port.out          EventRepository — what the core needs from storage
│   └── service           EventCatalogService implements the use cases
├── adapter
│   ├── in.web            REST controller, request/response DTOs, error handling
│   └── out.persistence   MongoDB documents, Spring Data repository, port adapter
└── config                composition root: the only place that wires the core into Spring
```

Dependencies point inwards: adapters → application → domain. `domain` and `application` contain no
Spring, Jakarta, Jackson or BSON classes. `HexagonalArchitectureTest` (ArchUnit) fails the build
otherwise.

Request flow: `EventController` → `CreateEventDraftUseCase` → `Event.draft(...)` (rules) →
`EventRepository.save` → `MongoEventRepositoryAdapter` → `events` collection.

## Tests

| Level | Class | Infrastructure |
|---|---|---|
| Domain rules | `EventTest` | none |
| Use cases | `EventCatalogServiceTest` | in-memory fake of `EventRepository` |
| Architecture | `HexagonalArchitectureTest` | none |
| Persistence adapter | `MongoEventRepositoryAdapterIT` | MongoDB via Testcontainers |
| HTTP end to end | `EventApiIT` | MongoDB via Testcontainers |

`*Test` run in `./mvnw test` (Surefire); `*IT` run in `./mvnw verify` (Failsafe) and need Docker.

## Running locally

From the repository root:

```sh
docker compose up -d                                   # MongoDB on localhost:27017
./mvnw -pl services/catalog-service spring-boot:run    # service on http://localhost:8081
```

Or run `TestCatalogServiceApplication` from the IDE — it starts MongoDB in a container by itself
(data is lost on stop). Sample requests: [`http/events.http`](http/events.http).

Inspect data: `docker compose exec mongodb mongosh catalog`, then `db.events.find()`.

| Setting | Default | Env variable |
|---|---|---|
| MongoDB URI | `mongodb://localhost:27017/catalog` | `MONGODB_URI` |
| HTTP port | `8081` | `SERVER_PORT` |
