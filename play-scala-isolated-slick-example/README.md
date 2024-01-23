# Play with Slick 3.3

This project shows Play working with Slick.

This project is configured to keep all the modules self-contained.

* Slick is isolated from Play, not using play-slick.
* Database migration is done using [Flyway](https://flywaydb.org/), not Play Evolutions.
* Slick's classes are auto-generated following database migration.

## Database Migration

```bash
sbt flyway/flywayMigrate
```

## Slick Code Generation

You will need to run the flywayMigrate task first, and then you will be able to generate tables using sbt-codegen.

```bash
sbt slickCodegen
```

## Testing

You can run functional tests against an in memory database and Slick easily with Play from a clean slate:

```bash
sbt clean flyway/flywayMigrate slickCodegen compile test
```

## Running

To run the project, start up Play:

```bash
sbt run
```

And that's it!

Now go to <http://localhost:9000>, and you will see the list of users in the database.

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).
