# ADR-0005: Apache Kafka as the event backbone

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Services exchange domain events. Some consumers need to replay history (e.g. rebuilding the search
read model), events must be ordered per aggregate, and on-sales produce bursts of traffic.

## Decision

We will use Apache Kafka as the event backbone. Event contracts are defined in Avro and managed by a
Schema Registry with backward-compatibility enforcement. Events for one aggregate share a partition
key to preserve ordering. Failed messages go to dead-letter topics after bounded retries.
In Kubernetes, Kafka runs in KRaft mode, managed by the Strimzi operator.

## Alternatives considered

- **RabbitMQ** — great for task queues and routing; lacks a durable, replayable log, which we need
  for read models and auditing.
- **Cloud-native queues (SQS/SNS, Pub/Sub)** — would tie the system to one cloud provider.

## Consequences

- Replayable history, high throughput, consumer groups for horizontal scaling.
- Consumers must be idempotent (at-least-once delivery).
- Operating Kafka is non-trivial; Strimzi reduces that burden.
