# Play with Slick 3.1

This project shows Play working with Slick.  

This project is configured to keep all the modules self-contained. 

* Slick is isolated from Play, not using play-slick.  
* Database migration is done using [Flyway](), not Play Evolutions.
* Slick's classes are auto-generated following database migration.

## Migration

```
sbt
clean
project flyway
flywayMigrate
```

## Running

To run the project, start up Play:

```
sbt run
```

And that's it! 
 
Now go to [http://localhost:9000](http://localhost:9000), and you will see the list of users in the database.
