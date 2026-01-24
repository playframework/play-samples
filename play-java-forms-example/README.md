# play-java-forms-example

This example shows form processing and form helper handling in Play.

## How to run

Start the Play app:

```bash
sbt run
```

And open <http://localhost:9000/>

## How to run on Windows server

How to take build:
1. Open Command Prompt (cmd).
2. Navigate to the project directory using the `cd` command.
3. Run the following command to start the Play app:
```
sbt clean compile
sbt dist
```

## How to run on Windows server:
1. Copy the generated zip file from `target/universal/` to your Windows server.
2. Extract the zip file to a desired location on the server.
3. Open Command Prompt (cmd) on the Windows server.
4. Navigate to the extracted directory using the `cd` command.
5. Run the following command to start the Play app:
```
bin\play-java-forms-example.bat -Dhttp.port=9000 -Dhttp.address=0.0.0.0
```

## Notes:
- Make sure your application.conf file is properly configured for production settings.

```
play.http.secret.key = "changeme1234567890abcdefghijklmnopqrstuvwxyz"
play.filters.hosts {
  allowed = ["."]
}
```

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Documentation

Please see <https://playframework.com/documentation/latest/JavaForms>.
