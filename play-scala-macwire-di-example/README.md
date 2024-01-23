# play-scala-macwire-di-example

This is an example project for setting up Play with Macwire compile time dependency injection.

For further details, please see:

* <https://www.playframework.com/documentation/latest/ScalaCompileTimeDependencyInjection>
* <https://github.com/adamw/macwire/blob/master/README.md>
* <https://di-in-scala.github.io/>

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
