# play-scala-slick-example

This project demonstrates how to create a simple CRUD application with [Play](https://www.playframework.com/) and
[Slick](https://scala-slick.org/) using
[Play-Slick](https://www.playframework.com/documentation/latest/PlaySlick).

To run the projects in this sample:

```bash
# Open the SBT shell
sbt 
# Once in the SBT shell list the projects
projects
# Select a project
project basic-sample
# Start the play dev server
run
```

To see an example of a Play application using Slick outside the play application lifecycle, please see:

`play-scala-isolated-slick-example` in https://github.com/playframework/play-samples/

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

