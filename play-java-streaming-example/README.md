# play-java-streaming-example

This is an example Play template that demonstrates Streaming with Server Sent Events or Comet, using Pekko Streams.

Please see the documentation at:

* <https://www.playframework.com/documentation/latest/JavaComet>

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
