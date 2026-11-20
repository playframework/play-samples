# Running with Docker

Run the commands below from the `java/grpc` sample directory. Install Docker with its
Buildx plugin, sbt, a JDK supported by this build (Java 17 or later), and OpenSSL.
The application image uses the Java 25 JRE from `eclipse-temurin:25-jre-resolute` (Ubuntu 26.04 LTS).

## Build the image

```bash
sbt Docker/publishLocal
```

sbt-native-packager generates the Dockerfile and launcher, packages the application,
and builds the local image `play-java-grpc-example:1.0-SNAPSHOT`. The generated image runs
as a non-root user. A separate Dockerfile or an additional Bash installation is not needed.

The base image supports ARM64 and AMD64. Docker selects the host architecture, so a build
on an ARM64 machine produces an ARM64 application image. Use an image matching the
architecture of the deployment nodes.

To publish an image for both architectures, use temporary sbt settings:

```bash
sbt 'set Docker / dockerBuildxPlatforms := Seq("linux/amd64", "linux/arm64")' \
    'set Docker / dockerRepository := Some("registry.example.org/my-organization")' \
    Docker/publish
```

Replace the registry with your own and authenticate with it before running the command.
The settings apply only to this sbt invocation, preserving the local image names used
elsewhere in this guide. Buildx builds both variants and pushes them with a shared image
index; its builder must support both target platforms. The `Docker/publishLocal` command
above builds the local image for the default platform.

## Start the application

Generate an application secret and start the container:

```bash
export APPLICATION_SECRET="$(openssl rand -hex 32)"
docker run --rm --name play-java-grpc-example \
  --publish 127.0.0.1:9000:9000 \
  --publish 127.0.0.1:9443:9443 \
  --env APPLICATION_SECRET \
  --env TRANSPORT_HTTP_BIND_ADDRESS=0.0.0.0 \
  --env TRANSPORT_HTTPS_BIND_ADDRESS=0.0.0.0 \
  --env DEPLOYMENT_SERVICE_NAME=localhost \
  play-java-grpc-example:1.0-SNAPSHOT \
  -Dplay.server.pidfile.path=/dev/null
```

Both server endpoints bind to the container's network interfaces. The gRPC client connects
to `localhost` inside the same container; the bind address `0.0.0.0` is not a client destination.
The published ports are accessible on the host's loopback interface. The PID file is disabled
because the container runtime manages the process and the container user cannot write to
the application directory.

From another terminal, send an HTTP request:

```bash
curl --fail http://localhost:9000/
```

The response is `Hello Caplin!`. This exercises the whole path: the HTTP controller calls
the gRPC service over HTTP/2 with TLS and returns its reply.

The HTTPS endpoint can also be used:

```bash
curl --fail --insecure https://localhost:9443/
```

The gRPC client explicitly trusts the bundled `selfsigned.crt` certificate.
This example ships a self-signed development certificate for `localhost`. The `--insecure`
flag is only for trying that certificate locally. Use an appropriate certificate and trust
configuration for a real deployment; see @ref[Networking](networking.md).

Stop the container from another terminal:

```bash
docker stop play-java-grpc-example
```

## Deploy to a cluster

See @ref[Kubernetes](kubernetes.md) or @ref[OpenShift](openshift.md) for image loading,
registry and deployment instructions. Those deployments configure the gRPC client to
connect through the Kubernetes Service instead of the container's loopback interface.
