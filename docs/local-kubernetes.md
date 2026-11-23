# Running on local Kubernetes (k3d + Helm)

The same Helm charts that deploy Ticketflow to the cloud also run on a laptop, in a k3d cluster
(k3s inside Docker containers, [ADR-0010](adr/0010-kubernetes-cloud-agnostic.md)).

## Prerequisites

Docker, and the pinned tools from `mise.toml`:

```sh
mise install        # k3d, helm, kubectl (plus JDK and Maven)
```

## Cluster

```sh
k3d cluster create --config k3d/cluster.yaml    # about 20 seconds
k3d cluster delete ticketflow                   # removes the cluster and all its data
```

Creating the cluster also points `kubectl` and `helm` at it (context `k3d-ticketflow`).
Pause and resume without losing anything: `k3d cluster stop ticketflow` / `k3d cluster start ticketflow`.

## Deploy Catalog

```sh
helm upgrade --install catalog-service charts/catalog-service --wait
```

This installs two workloads: the `catalog-service` Deployment and its MongoDB, the
`catalog-service-mongodb` StatefulSet with a 1 GiB persistent volume. The image comes from GHCR
(`latest`, built by CI from `main`). The same command applies later changes to the chart or values.

Use the API through a tunnel to the Service:

```sh
kubectl port-forward service/catalog-service 8081:80
```

Now http://localhost:8081/swagger-ui.html and the requests in
[`services/catalog-service/http/events.http`](../services/catalog-service/http/events.http) work
as they do with `spring-boot:run`.

### Deploy an image built from your working copy

```sh
docker build -f services/catalog-service/Dockerfile -t ticketflow-catalog-service:local .
k3d image import ticketflow-catalog-service:local -c ticketflow
helm upgrade --install catalog-service charts/catalog-service --wait --reuse-values \
  --set image.repository=ticketflow-catalog-service --set image.tag=local
```

`k3d image import` copies the image from Docker into the cluster node, so no registry is needed.
`--reuse-values` keeps the settings the release already has: with `--set` alone, Helm starts again
from the chart defaults and drops earlier overrides.
After rebuilding under the same tag, import again and restart the pods:
`kubectl rollout restart deployment/catalog-service`.

Back to the image from GHCR, keeping any other settings of the release:

```sh
helm upgrade catalog-service charts/catalog-service --wait --reuse-values \
  --set image.repository=ghcr.io/justprzemek/ticketflow-catalog-service --set image.tag=latest
```

## What the chart sets up

| Concern | How |
|---|---|
| Configuration | `values.yaml`; override with `-f <file>` or `--set`. Arbitrary environment variables through `env`. |
| Health | Startup and liveness probes call `/actuator/health/liveness`, readiness calls `/actuator/health/readiness`. |
| Updates without downtime | `maxUnavailable: 0`: the new pod must be ready before the old one is stopped. The old pod first waits 5 s (`preStop`) to drop out of the Service, then Spring Boot finishes in-flight requests. |
| Resources | CPU and memory requests and limits for both containers. |
| Security | Non-root users (1000 for the service, 999 for MongoDB), read-only root filesystem, no Linux capabilities, no privilege escalation, no Kubernetes API token. |
| Data | MongoDB keeps `/data/db` on a PersistentVolumeClaim, which outlives the pod and even `helm uninstall`. |

MongoDB runs without authentication and is reachable only inside the cluster. Credentials come
with the secrets management decision for the cloud environments.

## Useful commands

```sh
kubectl get pods,svc,pvc                               # what is running
kubectl logs deployment/catalog-service -f             # application logs
kubectl describe pod -l app.kubernetes.io/component=api   # events, probe failures, restarts
kubectl exec -it catalog-service-mongodb-0 -- mongosh catalog   # then: db.events.find()
helm get values catalog-service                        # values this release was installed with
helm template catalog-service charts/catalog-service   # render the manifests without installing
helm history catalog-service                           # revisions; `helm rollback catalog-service <n>`
```

Try it:

- **Self-healing:** `kubectl delete pod -l app.kubernetes.io/component=api` and watch a
  replacement start with `kubectl get pods -w`.
- **Persistence:** create an event, `kubectl delete pod catalog-service-mongodb-0`, read the event again.
- **Rolling update:** `helm upgrade catalog-service charts/catalog-service --reuse-values --set env.DEMO=1`
  and watch the new pod become ready before the old one terminates.

## Clean up

```sh
helm uninstall catalog-service                         # keeps the MongoDB volume
kubectl delete pvc data-catalog-service-mongodb-0      # deletes the data
```

Or simply delete the whole cluster.
