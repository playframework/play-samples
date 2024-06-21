# play-java-jpa-example

This project demonstrates how to create a simple database application with Play, using JPA.

Please see the Play documentation for more details:

* https://www.playframework.com/documentation/latest/JavaJPA
* https://www.playframework.com/documentation/latest/ThreadPools
* https://www.playframework.com/documentation/latest/JavaAsync

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
