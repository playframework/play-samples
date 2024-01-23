# play-scala-forms-example

This example shows form processing and form helper handling under Play.

## How to run

Start the Play app:

```bash
sbt run
```

And open [http://localhost:9000/](http://localhost:9000/)

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).


## Credits

Originally written by Chris Birchall and the Guardian Team: <https://github.com/cb372/play-forms-tutorial>.  Much thanks, especially for the [article](https://www.theguardian.com/info/developer-blog/2015/dec/30/how-to-add-a-form-to-a-play-application).
