# ADR-0010: Kubernetes everywhere, cloud-agnostic infrastructure

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

The system should run identically on a laptop and in the cloud, on free tiers, and be portable to a
different cloud provider later (e.g. AWS).

## Decision

- All workloads run on **Kubernetes**, packaged with **Helm**.
- **Local:** Docker Compose for the earliest phase, then **k3d** (k3s in Docker).
- **Cloud:** **k3s** on **Oracle Cloud Always Free** (ARM) hosts the `qa` and `prod` environments.
- **AWS EKS** is used only for short, sessional experiments and torn down afterwards.
- Infrastructure is provisioned with **Terraform**; stateful components run in-cluster via
  operators (CloudNativePG, Strimzi) rather than provider-specific managed services.

## Consequences

- Moving to another cloud means a new Terraform module; application manifests stay the same.
- Images must be built for `linux/amd64` and `linux/arm64`.
- We operate databases and Kafka ourselves — the managed vs. self-hosted trade-off is revisited
  if the system moves to AWS (RDS, MSK).
