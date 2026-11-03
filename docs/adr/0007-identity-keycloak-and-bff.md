# ADR-0007: Keycloak for identity, Spring Security resource servers and BFF

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

The platform has customers, organizers, admins and gate staff. Users need registration, login,
email verification and possibly MFA and social login. Services must authorize requests consistently.
Storing tokens in browser JavaScript is discouraged for SPAs.

## Decision

- **Keycloak** is the identity provider (OAuth2 / OpenID Connect). Ticketflow never stores passwords.
- Every backend service is a **Spring Security OAuth2 resource server** validating JWTs and applying
  role-based authorization (`CUSTOMER`, `ORGANIZER`, `ADMIN`, `GATE_STAFF`).
- The **API gateway acts as a Backend-for-Frontend (BFF)**: it performs the login flow, keeps
  tokens server-side and exposes only an HTTP-only session cookie to the Angular app, relaying
  tokens to downstream services.
- **Service-to-service** calls use the client-credentials grant.
- Domain data about users lives in the owning services, referenced by the user's subject ID (`sub`).

## Alternatives considered

- **Custom authentication with Spring Security** — reinventing password storage, recovery and MFA
  is risky and not how production systems are built.
- **SaaS identity (Auth0, Okta, Cognito)** — production-grade but vendor-specific or paid.

## Consequences

- Industry-standard security model.
- Keycloak is an additional stateful component to run and configure (realm as code).
