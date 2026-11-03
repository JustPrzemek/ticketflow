# ADR-0011: GitHub Actions for CI, Argo CD for GitOps delivery

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Every change should be built, tested, analyzed and delivered to environments automatically and
traceably, with promotion from `qa` to `prod`.

## Decision

- **CI:** GitHub Actions — build, unit/integration/contract tests, SonarQube Cloud quality gate,
  dependency and image scanning (Trivy), multi-arch image build pushed to GHCR.
- **CD:** Argo CD watches the desired state in Git (GitOps). Deployment = a commit changing an image
  version for an environment. `qa` is updated automatically; `prod` is promoted after E2E tests
  and manual approval.
- **Workflow:** trunk-based development with short-lived branches, pull requests, Conventional
  Commits, automated review (CodeRabbit), dependency updates (Renovate).

## Alternatives considered

- **Jenkins** — still widespread, but self-hosted and largely superseded by hosted CI in new projects.
- **Push-based CD from CI** — simpler, but the cluster state drifts from Git and credentials to the
  cluster live in CI.

## Consequences

- Git is the single source of truth for what runs where; rollbacks are reverts.
- Free for public repositories.
