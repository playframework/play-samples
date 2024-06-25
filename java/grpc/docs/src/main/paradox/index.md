# Play Java gRPC Example

This example application shows how to use Pekko gRPC to both expose and use gRPC services inside an Play application.

The [Play Framework](https://www.playframework.com/) combines productivity and performance making it easy to build
scalable web applications with Java and Scala. Play is developer friendly with a "just hit refresh" workflow and
built-in testing support. With Play, applications scale predictably due to a stateless and non-blocking architecture.

[Pekko gRPC](https://pekko.apache.org/docs/pekko-grpc/current/) is a toolkit for building streaming
gRPC servers and clients on top of Pekko Streams.

For detailed documentation refer to https://www.playframework.com/documentation/latest/Home and https://pekko.apache.org/docs/pekko-grpc/current/.

## Obtaining this example

You may download the code from [GitHub](https://github.com/playframework/play-java-grpc-example) directly or you can
kickstart your Play gRPC project on [Lightbend's Tech Hub](https://developer.lightbend.com/start/?group=play&project=play-java-grpc-example).

## What this example does

This example runs a Play application which serves both HTTP/1.1 and gRPC (over HTTP/2) enpoints. This application also
uses an Pekko-gRPC client to send a request to itself. When you sent a `GET` request `/` the request is handled by a
vanilla Play `Controller` that sends a request over gRPC to the gRPC endpoint:


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

When deploying this application on Kubernetes or Openshift, there are some extra considerations wrt request rounting.
Refer to @ref:[Networking](networking.md) for more details on how this sample works on production environments.

## Running

* Running on a cluster: refer to the specific guides for @ref:[OpenShift](openshift.md) and @ref:[Kubernetes (`minikube`)](kubernetes.md)
for specific information on deploying in Kubernetes-based clusters.

* Run @ref[locally](locally.md)


## Understanding the code

Refer to the @ref[understanding the code](code-details.md) for more details on how this example application works.


@@@ index

 * [Networking](networking.md)
 * [Running](running.md)
     * [Running on OpenShift](openshift.md)
     * [Running on Kubernetes (`minikube`)](kubernetes.md)
     * [Running locally](locally.md)
 * [understanding the code](code-details.md)

@@@
