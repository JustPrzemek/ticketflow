# ADR-0017: Maven instead of Gradle

- **Status:** Accepted — supersedes [ADR-0004](0004-gradle-monorepo.md) (build tool only; the monorepo stays)
- **Date:** 2026-11-05

## Context

ADR-0004 chose Gradle for build speed and flexible shared build logic. The project's focus is
distributed-systems architecture, not build tooling; Maven is the build tool the owner knows and
the most common one in enterprise Java.

## Decision

We will build the monorepo with **Maven** (multi-module): a root aggregator POM inheriting from
`spring-boot-starter-parent` holds shared versions, plugins and quality checks; each service is a
module. The Maven Wrapper (`./mvnw`) pins the Maven version; `mise.toml` pins the JDK.
CI builds only affected modules with `-pl <module> -am` when the monorepo grows.

## Consequences

- Lower learning overhead; conventions familiar to most Java teams.
- Slower multi-module builds than Gradle; mitigated by CI caching of `~/.m2` and building only
  affected modules.
