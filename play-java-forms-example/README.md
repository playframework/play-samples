# play-java-forms-example

This example shows form processing and form helper handling in Play.

## How to run

Start the Play app:

```bash
sbt run
```

And open <http://localhost:9000/>

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Documentation

Please see <https://playframework.com/documentation/latest/JavaForms>.
