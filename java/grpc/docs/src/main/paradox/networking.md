# Networking

This sample application serves both HTTP and HTTPS traffic in ports 9000 and 9443 respectively.
When deploying, there are 2 pods behind a Service exposed to the outside via an Ingress/Router. The
Service exposes both 9000 and 9443 but the Ingress/Router only expose the `PLAINTEXT` port. 

The Kubernetes and OpenShift descriptors create an Ingress or Route rules based on the
`myservice.example.org` virtual host. This means that any external request arriving into 
the cluster with a `Host: myservice.example.org` header will be forwarded to our 
`service/play-java-grpc-example`.


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

## TLS between the HTTP controller and the gRPC service

The gRPC client uses HTTP/2 with TLS. The server loads `conf/selfsigned.keystore`, which
contains this example's self-signed certificate for `localhost`. The matching public
certificate is exported as `conf/selfsigned.crt`; the client's `trusted = "/selfsigned.crt"`
setting loads it from the classpath. This allows the client to verify that certificate
without disabling certificate validation or modifying the JVM's global trust store.

When connecting through a Kubernetes Service, its DNS name differs from the certificate's
hostname. The client therefore uses `override-authority = "localhost"` for TLS hostname
verification and the HTTP/2 authority. Overriding the authority does not establish trust
by itself; both the trusted certificate and the matching hostname are required.

The included certificate and private key are for demonstrating the example. For a real
deployment, provision your own server keystore and the client's trusted CA certificate,
and configure the authority to match the server certificate. Kubernetes/OpenShift Secret
management and external HTTPS termination are outside this example's scope.
