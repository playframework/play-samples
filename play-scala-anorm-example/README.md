# play-scala-anorm-example

This is an example Play application that uses Scala on the front end, and communicates with an in memory database using Anorm.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Play

Play documentation is here:

<https://playframework.com/documentation/latest/Home>

## Anorm

Anorm is a Scala library that uses SQL.

- up to 2.5.3: <https://www.playframework.com/documentation/latest/ScalaAnorm>
- 2.6+: <https://playframework.github.io/anorm/>
