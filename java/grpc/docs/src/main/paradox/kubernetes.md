# Deploy on Kubernetes

## Prerequisites

Install Docker, sbt, a supported JDK, OpenSSL, [kubectl](https://kubernetes.io/docs/tasks/tools/),
and [Minikube](https://minikube.sigs.k8s.io/docs/start/).
Run the commands below from the `java/grpc` sample directory.

## Build and load the image

Start Minikube and build the image with the host's Docker daemon:

```bash
minikube start --driver=docker
sbt Docker/publishLocal
minikube image load play-java-grpc-example:1.0-SNAPSHOT
```

`minikube image load` imports the image into the cluster's container runtime, including
containerd. It does not require switching the shell to Minikube's Docker daemon.
The application image must match the architecture of the cluster nodes; see
@ref[Running with Docker](docker.md).

## Deploy

Create a namespace and the application secret referenced by the Deployment:

```bash
kubectl create namespace play-java-grpc-example
kubectl create secret generic play-java-grpc-example \
  --namespace play-java-grpc-example \
  --from-literal=application-secret="$(openssl rand -hex 32)"
kubectl apply --namespace play-java-grpc-example -k deployment/overlays/minikube
kubectl rollout status --namespace play-java-grpc-example \
  deployment/play-java-grpc-example-v1-0-snapshot
```

The Deployment starts two replicas. The Service exposes HTTP on port 9000 and HTTPS/gRPC
on port 9443. Readiness checks keep pods out of the Service until their HTTPS endpoint is
listening. Both replicas use the same application secret. Both server ports bind to
`0.0.0.0`, allowing Service traffic and `kubectl port-forward` connections.

## Verify the application

Forward the Service's HTTP port:

```bash
kubectl port-forward --namespace play-java-grpc-example service/play-java-grpc-example 9000:9000
```

From another terminal:

```bash
curl --fail --header 'Host: myservice.example.org' http://localhost:9000/
```

The response is `Hello Caplin!`. The HTTP controller calls the gRPC service through
Kubernetes Service discovery before returning the reply.

The overlay also includes a `networking.k8s.io/v1` Ingress for `myservice.example.org`.
To use it, configure an Ingress controller and its default IngressClass, or set
`spec.ingressClassName` in `deployment/overlays/minikube/ingress.yml` to your controller's class.
Point that hostname at the controller's address. If you change the hostname, also update
the allowed host in the Deployment's `JAVA_OPTS`.

## Remove the example

Delete the namespace created above when finished:

```bash
kubectl delete namespace play-java-grpc-example
```
