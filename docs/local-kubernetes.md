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

Two ways, side by side in one cluster:

- **By hand with Helm** (this section): release in the `default` namespace — for trying out a chart
  change or an image from your working copy before anything goes into Git.
- **GitOps with Argo CD** ([below](#gitops-with-argo-cd)): `qa` and `prod` namespaces, exactly as on
  the server, driven by [`ticketflow-deploy`](https://github.com/JustPrzemek/ticketflow-deploy).

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

MongoDB runs without authentication and is reachable only inside the cluster. In the cloud
environments credentials come from Sealed Secrets ([ADR-0019](adr/0019-gitops-repository-and-secrets.md)).

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

## GitOps with Argo CD

The same setup as on the server (ADR-0011, ADR-0019). From the `ticketflow-deploy` clone:

```sh
./platform/bootstrap.sh                                   # Argo CD + root Application, once per cluster
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d
kubectl -n argocd port-forward svc/argocd-server 8080:80  # keeps running; UI at http://localhost:8080, user admin
```

`port-forward` holds the terminal until Ctrl+C, so it goes last (or into its own terminal).

Argo CD then syncs `platform/apps` from `main`: itself, the Sealed Secrets controller and one
Application per `environments/<env>/<service>/values.yaml` (`catalog-service-qa`, `catalog-service-prod`).
Details: [`platform/README.md`](https://github.com/JustPrzemek/ticketflow-deploy/blob/main/platform/README.md).

Try it:

- **Self-heal:** `kubectl -n qa scale deploy catalog-service --replicas=3` — Argo CD scales it back.
- **Deploy:** set `replicaCount: 2` in `environments/qa/catalog-service/values.yaml`, merge the pull
  request, press *Refresh* in the UI (or wait ~3 min) — a second pod starts on `qa`, `prod` is untouched.
- **Rollback:** `git revert` that commit in a new pull request and merge it.

Do not `helm` or `kubectl apply` anything Argo CD manages — it reverts manual changes.

## Clean up

```sh
helm uninstall catalog-service                         # keeps the MongoDB volume
kubectl delete pvc data-catalog-service-mongodb-0      # deletes the data
```

Or simply delete the whole cluster.
