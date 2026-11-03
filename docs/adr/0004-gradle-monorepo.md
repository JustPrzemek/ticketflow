# ADR-0004: Gradle (Kotlin DSL) monorepo

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

The system consists of many services sharing conventions (Java version, plugins, quality checks,
test setup) and shared event contracts. One developer works across all of them.

## Decision

We will keep all services, shared libraries, infrastructure code and documentation in a single
repository built with Gradle (Kotlin DSL). Shared build logic lives in convention plugins
(`build-logic`). CI builds and tests only the projects affected by a change.

## Alternatives considered

- **Maven** — the most common build tool in enterprise Java; simpler, but slower for multi-module
  builds and less flexible for shared build logic and incremental builds.
- **Polyrepo** — realistic for large organizations with many teams, but creates heavy
  coordination overhead for a single developer.
- **Bazel** — used by very large monorepos; too heavy for this project.

## Consequences

- Atomic changes across services and contracts in one PR.
- Build caching and incremental builds keep CI fast.
- Discipline required: services must not depend on each other's internals — enforced by Gradle
  module boundaries and ArchUnit.
