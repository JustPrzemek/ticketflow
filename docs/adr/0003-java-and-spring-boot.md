# ADR-0003: Java 25 and Spring Boot 4 for backend services

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Backend services need a mature ecosystem for web APIs, security, messaging, data access and
observability. The team's existing expertise is Java and Spring Boot.

## Decision

We will implement backend services in Java 25 (LTS) with Spring Boot 4, using virtual threads where
they simplify blocking I/O.

## Alternatives considered

- **Go** — excellent for small, fast services; may be used later for a single service as an
  experiment, but not as the primary stack.
- **Kotlin** — attractive, but adds a learning axis that is not a project goal.

## Consequences

- First-class integrations: Spring Kafka, Spring Security (resource server, OAuth2 client),
  Spring Data, Micrometer/OpenTelemetry, Spring AI.
- Widely used in industry (including large streaming and e-commerce platforms).
- JVM memory footprint must be tuned for running many services locally.
