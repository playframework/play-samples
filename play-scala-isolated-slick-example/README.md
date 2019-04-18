# Play with Slick 3.1

[![Build Status](https://travis-ci.org/playframework/play-scala-isolated-slick-example.svg?branch=2.6.x)](https://travis-ci.org/playframework/play-scala-isolated-slick-example)

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
