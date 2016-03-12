# Play 2.5 with Slick 3.1

This project shows Play 2.5 working with Slick.  

This project is configured to keep all the modules self-contained.  

* Slick is isolated from Play, not using play-slick.  
* Database migration is done using [Flyway](), not Play Evolutions.
* Slick's classes are auto-generated following database migration.

## Database

The sample application is configured to use PostgreSQL and has some custom drivers to make Postgres / Slick integration easier.

If you are using PostgreSQL for the first time, follow the instructions to [install on Mac using HomeBrew](http://exponential.io/blog/2015/02/21/install-postgresql-on-mac-os-x-via-brew/), and then start up PostgreSQL.

```
postgres -D /usr/local/var/postgres
```

```
sudo su - postgres # if on Linux
```

The default Postgres user and password for this application is "myuser/mypass", db is "myapp":

```
createuser myuser
createdb myapp
```

### Database Migration

Start up `sbt` and go into the flyway module to run database migrations:

```
project flyway
flywayMigrate
```

Please see the [flyways documentation](http://flywaydb.org/getstarted/firststeps/sbt.html) for more details.

## User DAO

The module is defined with a public API in "modules/api":

```scala
package com.example.user

trait UserDAO {

  def lookup(id: String): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user:User)

  def delete(id:String)

  def create(user:User): Future[Int]

}

case class User(id:String, email:String)

trait UserDAOExecutionContext extends ExecutionContext
```

Play works with the DAO by installing the `SlickUserModule` and assigning a custom execution context:

```
class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[Config]).toInstance(configuration.underlying)
    bind(classOf[UserDAOExecutionContext]).toProvider(classOf[SlickUserDAOExecutionContextProvider])

    install(new SlickUserModule)
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}
```

The DAO must be closed to release JDBC connections, and this is handled through `UserDAOCloseHook`:

```scala
class UserDAOCloseHook @Inject() (dao: UserDAO, lifecycle: ApplicationLifecycle) {
  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Now closing database connections!")
      dao.close()
    }
  }
}
```

## Slick 

Slick configuration is simple.  The `User` case class is mapped to a `Users` table and the queries are implemented

```
@Singleton
class SlickUserDAO @Inject()(db: Database) extends UserDAO {

  import MyPostgresDriver.api._

  private val users: TableQuery[Users] = TableQuery[Users]

  // ...
  
  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[String]("id", O.PrimaryKey)

    def email = column[String]("email")

    def * = (id, email) <>(User.tupled, User.unapply)
  }
}
```

Slick [schema code generation](http://slick.typesafe.com/doc/3.1.0/code-generation.html) tool, which will create the `Tables` object under `target/scala-2.11/src_managed`.



## Running

To run the project, start up Play:

```
project play
run
```

