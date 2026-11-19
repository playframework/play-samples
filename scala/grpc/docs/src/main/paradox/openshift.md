# Deploy on OpenShift

## Prerequisites

Use an OpenShift 4 cluster, a matching [`oc` CLI](https://docs.redhat.com/en/documentation/openshift_container_platform/4.20/html/cli_tools/openshift-cli-oc),
Docker, sbt, a supported JDK, OpenSSL, and a container registry accessible from the cluster.
Run the commands below from the `scala/grpc` sample directory.

## Build and push the image

Replace the server and registry placeholders with your own values:

```bash
export OPENSHIFT_SERVER=https://api.my-cluster.example.org:6443
export OPENSHIFT_PROJECT=play-scala-grpc-example
export IMAGE=play-scala-grpc-example
export TAG=1.0-SNAPSHOT
export DOCKER_REGISTRY_SERVER=registry.example.org
export DOCKER_REGISTRY="$DOCKER_REGISTRY_SERVER/my-organization"

oc login "$OPENSHIFT_SERVER"
oc new-project "$OPENSHIFT_PROJECT"
sbt Docker/publishLocal
docker login "$DOCKER_REGISTRY_SERVER"
docker tag "$IMAGE:$TAG" "$DOCKER_REGISTRY/$IMAGE:$TAG"
docker push "$DOCKER_REGISTRY/$IMAGE:$TAG"
```

The image must match the architecture of the deployment nodes. A local ARM64 build creates
an ARM64 application image; see @ref[Running with Docker](docker.md).
If the registry is private, configure an image pull secret for the project's service account.

## Configure the deployment

Edit `deployment/overlays/my-openshift-cluster/kustomization.yaml` so that `images.newName`
and `images.newTag` match the image pushed above. Set `spec.host` in the overlay's `route.yml`
to a hostname routed to your OpenShift router. Set the allowed host in `JAVA_OPTS` in
the overlay's `deployment-patch.yml` to match that hostname. These hostname changes
apply only to the OpenShift overlay.

The Deployment requires a non-root user, disables privilege escalation, drops Linux
capabilities and uses the runtime's default seccomp profile. It leaves the UID unspecified
so OpenShift can assign it. The launcher and application files are readable without writing
to the application directory. The Deployment disables the PID file so no writable
application directory is needed.

Create the application secret and apply the overlay:

```bash
oc create secret generic "$IMAGE" \
  --namespace "$OPENSHIFT_PROJECT" \
  --from-literal=application-secret="$(openssl rand -hex 32)"
oc apply --namespace "$OPENSHIFT_PROJECT" -k deployment/overlays/my-openshift-cluster
oc rollout status --namespace "$OPENSHIFT_PROJECT" \
  deployment/play-scala-grpc-example-v1-0-snapshot
```

## Verify the application

```bash
ROUTE_HOST="$(oc get route play-scala-grpc-route \
  --namespace "$OPENSHIFT_PROJECT" --output jsonpath='{.spec.host}')"
curl --fail "http://$ROUTE_HOST/"
```

The response is `Hello, Caplin!`. The external Route uses HTTP; the controller calls the
gRPC service inside the cluster using HTTP/2 with TLS. The included certificate is a
self-signed development certificate; see @ref[Networking](networking.md) before using
this setup for a real deployment.
