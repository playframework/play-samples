# Deploy on Kubernetes


### Prerequisites

Install the following:

* [Docker](https://docs.docker.com/install/)
* [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Minikube](https://github.com/kubernetes/minikube)
* [Sbt](https://www.scala-sbt.org/)


### Running

Once minikube is running the application can be deployed using:

```
$ eval $(minikube docker-env)
$ sbt docker:publishLocal
$ kubectl apply -f kubernetes/playgrpc.yml
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
$ curl -H "Host: myservice.example.org"  http://`minikube ip`/ping
pong
```

## Networking 

The Kubernetes descriptor creates an Ingress rule based on the `myservice.example.org` virtual host. That external 
request sent via `curl` uses plaintext `HTTP/1.1` and hits the `HomeController`. The code in the `HomeController` then
uses a gRPC client to send a request to itself (configured in `application.conf` using `RP_KUBERNETES_POD_NAME`). The
added complexity is that the Play process is using a fake certificate issued to `localhost`. If the gRPC request
was sent to `Host: my-pod-name` the TLS handshake would fail so we hardcode the `Authority`. Summing up: the 
`HomeController` opens a socket to itself for `HTTP/2+TLS` but sends a request to the virtual `Host: localhost` so 
the TLS handshake passes the hostname verification.     

```
                   ---------------
                   |              |
 -- (HTTP/1.1) --> > Controller  --> --+
                   |              |    |
                   |              |    |
         +-------> > gRPC Router  |    |
         |         |              |    |
         |         ----------------    |
         |                             |
         +------------ (HTTP/2) -------+
```



## Using TLS on Kubernetes

It is out of the scope of this sample application to demonstrate how to use a CA and a server certificate issued by 
the Kubernetes cluster Secret manager. Instead, a previously faked root CA and a server certificate are shipped with 
the application.