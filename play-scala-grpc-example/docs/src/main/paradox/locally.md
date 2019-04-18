# Running Locally

Running this application requires [sbt](http://www.scala-sbt.org/). gRPC, in turn, requires the transport to be 
HTTP/2 so we want Play to use HTTP/2. On top of that, we will also enable HTTPS. These requirements limit which 
setups are supported to run Play and only the following can be used at the moment:

1. you may use `sbt runProd` to run Play locally in a forked JVM in PROD mode, or
1. you may use `./ssl-play run` to run Play in DEV mode within `sbt`.

`./ssl-play` is a wrapper script around `sbt` that sets up the ALPN agent (required for HTTP/2) on the JVM running `sbt`.  

In both execution modes above, `sbt` will also generate the server and client sources based on the `app/protobuf/*.proto` 
files. The code generation happens thanks to the Akka gRPC plugin being enabled. See 
@ref[understanding the code](code-details.md) for more details. 

Finally, for your convenience, a self-signed certificate for `CN='localhost'` is provided in this 
example (see `conf/selfsigned.keystore`). Setting up a keystore works different in DEV mode and PROD mode. Locate 
the `play.server.https.keyStore.path` setting in `application.conf` and `build.sbt` for an example on how to set 
the keystore on each environment.
s
## Verifying

Finally, since now we know what the application is: an HTTP endpoint that hits its own gRPC endpoint to reply to the incoming request. 
We can trigger such request and see it correctly reply with a "Hello Caplin!" (which is the name of a nice Capybara, google it):

```
$ curl --insecure https://localhost:9443 ; echo
Hello Caplin!
```
