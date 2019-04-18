# Networking

This sample application serves both HTTP and HTTPS traffic in ports 9000 and 9443 respectively.
When deploying, there are 2 pods behind a Service exposed to the outside via an Ingress/Router. The
Service exposes both 9000 and 9443 but the Ingress/Router only expose the `PLAINTEXT` port. 

The Kubernetes and OpenShift descriptors create an Ingress or Route rules based on the
`myservice.example.org` virtual host. This means that any external request arriving into 
the cluster with a `Host: myservice.example.org` header will be forwarded to our 
`service/play-scala-grpc-example`. 


```
       -----                 +---+               
       | I |                 | S |    +--------------+
       | N |                 | E |    |              |
inet --| G |-- (HTTP/1.1) -->| R |---->  Controller ->----+
       | R |                 | V |    |              |    |
       | E |                 | I |    |              |    |
       | S |            +--->| C |----> gRPC Router  |    |
       | S |            |    | E |    |              |    |
       -----            |    +---+    +--------------+    |
                        |                                 |
                        +---------------- (HTTP/2) -------+
                
```


The code in the `HomeController`, uses a gRPC client to connect to a gRPC Router running on 
the same process. The gRPC client is configured to connect to the Service instead of connecting 
to the same pod where it running (see the client configuration in `application.conf` using 
`DEPLOYMENT_SERVICE_NAME `). 

@@@ note
You can find the deployment descriptors on the `deployment/` folder of this sample application.
@@@

## `use-tls = true`

This sample demonstrates gRPC over `CYPHERTEXT HTTP/2` so we pay the price of 
some added complexity: the Play process is using a self-signed certificate issued to 
`localhost`. The consequence of using a certificate issued to `localhost` is that the TLS handshake between the gRPC client 
running inside the `HomeController` and the Play server running the gRPC Router will only 
succeed if the requests include `Host: localhost` as a header. If the gRPC request was sent to 
`Host: my-service-name` the TLS handshake would fail. Therefor we hardcode the `Authority` 
to `localhost`. Summing up: the `HomeController` opens a socket to the service public IP 
for `HTTP/2 with TLS` but sends a request with the header `Host: localhost` so the TLS handshake 
passes the hostname verification.     

#### Using TLS on Kubernetes/OpenShift

It is out of the scope of this sample application to demonstrate how to use a CA and 
a server certificate issued by the Kubernetes/OpenShift Secret manager. Instead, a 
previously crafted, self-signed certificate are shipped with the application.
