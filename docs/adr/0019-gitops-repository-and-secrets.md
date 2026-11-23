# ADR-0019: GitOps repository layout and secrets management

- **Status:** Accepted
- **Date:** 2026-11-23

## Context

ADR-0011 makes Argo CD deliver what Git describes: a deployment is a commit that changes the
version running in an environment. Before Argo CD is installed (week 2) we need to decide:

- where the desired state of `qa` and `prod` lives,
- how the two environments differ,
- where the deployed version is recorded,
- how secrets (first: MongoDB credentials) get into Git without being readable,
- how the cluster pulls images from GHCR.

Constraints: `main` of this repo is protected (changes only through pull requests), everything must
stay on free tiers, and the setup must survive moving to another cloud (ADR-0010).

## Decision

### Separate public repository for the desired state

We will keep the desired state of the environments in a separate public repository,
[`JustPrzemek/ticketflow-deploy`](https://github.com/JustPrzemek/ticketflow-deploy).
This repository (`ticketflow`) keeps the code and the Helm charts; `ticketflow-deploy` only says
*which version runs where and with which settings*.

```text
ticketflow-deploy/
├── environments/
│   ├── qa/
│   │   └── catalog-service/
│   │       ├── values.yaml          # qa overrides + deployed version
│   │       └── sealed-secrets.yaml  # encrypted secrets for qa (when needed)
│   └── prod/
│       └── catalog-service/
│           └── ...
└── platform/                        # cluster add-ons: Argo CD apps, Sealed Secrets controller (#27)
```

- **Environments differ only by values.** The chart (`charts/<service>` here) is the same for every
  environment; `environments/<env>/<service>/values.yaml` holds only what differs (replicas,
  resources, URLs, the version).
- **The version is the full commit SHA** of this repository. It is the image tag CI already
  publishes (`ghcr.io/justprzemek/ticketflow-<service>:<sha>`) and the Git revision of the chart, so
  code and chart of one commit are always deployed together. `latest` is never used outside a laptop.
- **qa follows `main` automatically:** after an image is published, CI commits the new SHA to
  `environments/qa/` in `ticketflow-deploy`.
- **prod is promoted by a pull request** in `ticketflow-deploy` that copies the SHA verified on `qa`.
  Merging it is the manual approval (details in the promotion task, week 2).

### Secrets: Sealed Secrets

We will encrypt secrets with [Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets):

1. A controller in the cluster owns a key pair; its public key can be shared.
2. A secret is encrypted on the laptop with `kubeseal` using the public key and committed as a
   `SealedSecret` to `ticketflow-deploy`. Only the controller's private key can decrypt it.
3. Argo CD applies the `SealedSecret`; the controller decrypts it into an ordinary Kubernetes
   `Secret`, which the application reads as usual.

- A sealed secret is bound to its namespace and name, so `qa` and `prod` have separately sealed
  values — a `qa` secret cannot be copied to `prod`.
- The controller's private keys are backed up **outside Git** (password manager). Without them a
  rebuilt cluster cannot decrypt the committed secrets, and every secret must be sealed again.
  By default the controller adds a new sealing key every 30 days and keeps the old ones, so the
  backup must contain **all** keys (selected by the `sealedsecrets.bitnami.com/sealed-secrets-key`
  label) and be refreshed after each renewal; the exact steps go into the runbook with the
  installation. The backup is as sensitive as all the secrets together: whoever has it can decrypt every
  committed `SealedSecret` outside the cluster, so it is never shared and never stored in plain text.
- Charts never contain secret values; they reference an existing `Secret` by name
  (e.g. `mongodb.auth.existingSecret` for the MongoDB credentials, added when Catalog is first
  deployed to the cloud). Locally (k3d) MongoDB stays without authentication.

### Image pulls

The GHCR packages are **public**, so the cluster pulls images anonymously and needs no pull secret.
CI publishes with the built-in `GITHUB_TOKEN`; nothing else has write access to the registry.

## Alternatives considered

- **Environments in a directory of this repository** — one place for everything, but CI could not
  record a new `qa` version on the protected `main` without opening a second pull request per
  merge, and deployment commits would mix with code history. The Argo CD documentation recommends
  a separate configuration repository for the same reasons.
- **Secrets created by hand with `kubectl create secret`** — simplest, but the secret is not in Git,
  so a rebuilt cluster depends on someone remembering what was there. Breaks GitOps.
- **SOPS (with age)** — also encrypted files in Git, decryptable by us with our own key; Argo CD
  needs an extra plugin (KSOPS or helm-secrets) to decrypt them. More moving parts for the same
  result.
- **External Secrets Operator with a vault** (Oracle Cloud Vault, AWS Secrets Manager, HashiCorp
  Vault / OpenBao) — secrets live outside Git and are rotated in one place; the common choice in
  large organizations. Rejected for now: the cluster needs a credential to the vault ("secret
  zero"), cloud IAM setup, and it ties the setup to one provider. **Deferred as a stretch item**
  (Stretch epic on the board, alongside the items of ADR-0016): applications read a plain `Secret` either way, so switching later changes only where
  the `Secret` comes from.
- **Private GHCR packages with an image pull secret** — no benefit for a public codebase, one more
  secret per namespace to manage.

## Consequences

- Git describes what runs where; a deployment and a rollback are commits in `ticketflow-deploy`, and
  its history is the deployment log.
- CI needs write access to `ticketflow-deploy` (a token or deploy key stored as a GitHub Actions
  secret) — set up together with the promotion workflow.
- Two repositories to keep in mind: a chart change is merged here first and reaches an environment
  only when its SHA is deployed there.
- One more component in the cluster (the Sealed Secrets controller) and one more key to back up.
