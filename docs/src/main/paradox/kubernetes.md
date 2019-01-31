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
$ kubectl apply -f deployment/kubernetes-play-scala-grpc-example.ymls
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

## Networking 

The Kubernetes descriptor creates an Ingress rule based on the `myservice.example.org` virtual host. This 
means that any incoming request with `Host: myservice.example.org` will be forwarded to our 
`service/play-scala-grpc-example`. The externally exposed port uses plaintext `HTTP/1.1` and hits 
the `HomeController`. The code in the `HomeController`, then, uses a gRPC client to connect to a gRPC Router 
running on the same process (see the client configuration in `application.conf` using `RP_KUBERNETES_POD_NAME`). 

This sample exposes gRPC over CYPHERTEXT HTTP/2. In this example we pay the price of some added complexity: the 
Play process is using a fake certificate issued to `localhost` (the certificate is issued by a self-signed CA). The consequence of using a certificate issued to `localhost` is that the TLS handshake will only succeed if the 
requests include `Host: localhost` as a header. If the gRPC request was sent to `Host: my-pod-name` the TLS 
handshake would fail. Therefor we hardcode the `Authority` to `localhost`. Summing up: the `HomeController` 
opens a socket to the pod's public IP for `HTTP/2 with TLS` but sends a request to `Host: localhost` so the 
TLS handshake passes the hostname verification.     

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
