# Deploy on Kubernetes


### Prerequisites

Install the following:

* [Docker](https://docs.docker.com/install/)
* [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Minikube](https://github.com/kubernetes/minikube)
* [`kustomize`](https://github.com/kubernetes-sigs/kustomize) (v2.0.0+)
* [Sbt](https://www.scala-sbt.org/)


### Running

Once minikube is running the application can be deployed. Create the image:

```bash
$ eval $(minikube docker-env)
$ sbt docker:publishLocal
```

Apply the `Deployment`, the `Service` and the `Ingress` into your `minikube` cluster:   

```bash
$ kustomize build deployment/overlays/minikube | kubectl apply -f -
```

Verify the deployment status:

```
$ kubectl get all
NAME                                                         READY   STATUS    RESTARTS   AGE
pod/play-scala-grpc-example-v1-0-snapshot-6c7b575d86-9ql9r   1/1     Running   0          3m
pod/play-scala-grpc-example-v1-0-snapshot-6c7b575d86-jlsfq   1/1     Running   0          3m

NAME                              TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)               AGE
service/kubernetes                ClusterIP   10.96.0.1       <none>        443/TCP               17h
service/play-scala-grpc-example   ClusterIP   10.106.226.87   <none>        9000/TCP,9443/TCP     3m

NAME                                                    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/play-scala-grpc-example-v1-0-snapshot   2         2         2            2           3m

NAME                                                               DESIRED   CURRENT   READY   AGE
replicaset.apps/play-scala-grpc-example-v1-0-snapshot-6c7b575d86   2         2         2       3m
```

And send a request:

```
$ curl -H "Host: myservice.example.org"  http://`minikube ip`/
Hello, Caplin!
```

