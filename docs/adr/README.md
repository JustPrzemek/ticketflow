# Architecture Decision Records

New decision → copy [`template.md`](template.md) to `NNNN-short-title.md` and open a PR.

| ADR | Title | Status |
|---|---|---|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [0002](0002-event-driven-microservices.md) | Event-driven microservices aligned with bounded contexts | Accepted |
| [0003](0003-java-and-spring-boot.md) | Java 25 and Spring Boot 4 for backend services | Accepted |
| [0004](0004-gradle-monorepo.md) | Gradle (Kotlin DSL) monorepo | Superseded by [0017](0017-maven-instead-of-gradle.md) |
| [0005](0005-kafka-as-event-backbone.md) | Apache Kafka as the event backbone | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0006](0006-polyglot-persistence.md) | Database per service with polyglot persistence | Accepted |
| [0007](0007-identity-keycloak-and-bff.md) | Keycloak for identity, Spring Security resource servers and BFF | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0008](0008-transactional-outbox.md) | Transactional outbox for reliable event publishing | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0009](0009-cqrs-and-read-replicas.md) | CQRS read models and PostgreSQL read replicas | Accepted |
| [0010](0010-kubernetes-cloud-agnostic.md) | Kubernetes everywhere, cloud-agnostic infrastructure | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0011](0011-ci-github-actions-cd-argocd.md) | GitHub Actions for CI, Argo CD for GitOps delivery | Accepted |
| [0012](0012-stripe-connect-payments.md) | Stripe Connect for marketplace payments | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0013](0013-purchase-limit-enforcement.md) | Purchase limits enforced per account and card fingerprint | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0014](0014-virtual-waiting-room.md) | Virtual waiting room for every on-sale | Accepted |
| [0015](0015-signed-qr-tickets.md) | Cryptographically signed QR tickets with optional gate validation | Accepted, partially deferred by [0016](0016-first-iteration-scope.md) |
| [0016](0016-first-iteration-scope.md) | Scope of the first iteration (8 weeks) | Accepted |
| [0017](0017-maven-instead-of-gradle.md) | Maven instead of Gradle | Accepted |
| [0018](0018-architecture-style-per-service.md) | Code architecture style per service | Accepted |
