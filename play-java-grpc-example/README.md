# Play Java gRPC Example

This example is described in the [Play Java gRPC Example site](https://developer.lightbend.com/guides/play-java-grpc-example/).

This is an example application that shows how to use Pekko gRPC to both expose and use gRPC services inside an Play application.

For detailed documentation refer to https://www.playframework.com/documentation/latest/Home and https://pekko.apache.org/docs/pekko-grpc/current/.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Sample license

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.
