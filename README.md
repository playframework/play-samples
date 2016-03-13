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
createdb myapp
createuser --pwprompt myuser
```

### Database Migration

The first thing to do is to run the database scripts.  Flyways has a number of advantages over Play Evolutions: it allows for both Java migrations and SQL migrations, and has command line support.  

There are two migration scripts that will be run, `create_users` which adds a "users" table.

```
create table "users" (
  "id" UUID PRIMARY KEY NOT NULL,
  "email" VARCHAR(1024) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NULL
);
```

and `add_user` which inserts a single user into the table:

```
INSERT INTO "users" VALUES (
  'd074bce8-a8ca-49ec-9225-a50ffe83dc2f',
  'myuser@example.com',
  (TIMESTAMPTZ '2013-03-26T17:50:06Z')
  );
```

Flyway also allows repeatable migrations that can be Java or Scala based:

```scala
class R__AddCurrentUpdatedAtTimestamp extends JdbcMigration {

  override def migrate(c: Connection): Unit = {
    val statement = c.prepareStatement("UPDATE users SET updated_at = ?")
    try {
      statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      statement.execute()
    } finally {
      statement.close()
    }
  }
}
```

Start up `sbt` and go into the flyway module to run database migrations:

```
project flyway
flywayMigrate
```

See [the flyways documentation](http://flywaydb.org/getstarted/firststeps/sbt.html) for more details.

## User DAO

The module is defined with a public API in "modules/api" -- everything returns a Future, and there are custom [Joda Time](http://www.joda.org/joda-time/) `DateTime` objects which aren't mapped through Slick by default.

```scala
trait UserDAO {

  def lookup(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Option[User]]

  def all(implicit ec: UserDAOExecutionContext): Future[Seq[User]]

  def update(user: User)(implicit ec: UserDAOExecutionContext): Future[Int]

  def delete(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Int]

  def create(user: User)(implicit ec: UserDAOExecutionContext): Future[Int]

  def close(): Future[Unit]
}

case class User(id: UUID, email: String, createdAt: DateTime, updatedAt: Option[DateTime])

trait UserDAOExecutionContext extends ExecutionContext
```

## Slick 

The Postgres Driver for Slick is configured with [slick-pg](https://github.com/tminglei/slick-pg), which allows for custom mapping between PostgreSQL data types and Joda Time data types:

```scala
trait MyPostgresDriver extends ExPostgresDriver
with PgArraySupport
with PgDateSupportJoda
with PgPlayJsonSupport {

  object MyAPI extends API with DateTimeImplicits with JsonImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }

  // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"
  def pgjson = "jsonb"

  override val api = MyAPI
}

object MyPostgresDriver extends MyPostgresDriver
```

Slick configuration is simple, because the Slick [schema code generation](http://slick.typesafe.com/doc/3.1.0/code-generation.html) will look at the tables created from Flyway, and automatically generate a `Tables` trait.  From there, `UsersRow` and `Users` are created automatically.  Some conversion code is necessary to map between `UsersRow` and `User`. 

```scala
@Singleton
class SlickUserDAO @Inject()(db: Database) extends UserDAO with Tables {

  // Use the custom postgresql driver.
  override val profile: JdbcProfile = MyPostgresDriver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Users.filter(_.id === id))

  def lookup(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser(_)))
  }

  def all(implicit ec: UserDAOExecutionContext): Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser(_)))
  }

  def update(user: User)(implicit ec: UserDAOExecutionContext): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User)(implicit ec: UserDAOExecutionContext): Future[Int] = {
    db.run(
      Users += userToUsersRow(user.copy(createdAt = DateTime.now()))
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user:User): UsersRow = {
    UsersRow(user.id, user.email, user.createdAt, user.updatedAt)
  }

  private def usersRowToUser(usersRow:UsersRow): User = {
    User(usersRow.id, usersRow.email, usersRow.createdAt, usersRow.updatedAt)
  }
}
```

Once `SlickUserDAO` is compiled, everything is available to be bound and run in the Play application.

## Play

The root `Module.scala` file contains all the classes need to bind Slick and expose it as a `UserDAO`:

```scala
class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[Config]).toInstance(configuration.underlying)
    bind(classOf[UserDAOExecutionContext]).toProvider(classOf[SlickUserDAOExecutionContextProvider])

    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserDAO]).to(classOf[SlickUserDAO])

    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}
```

There are a couple of providers to do a "lazy get" of the database and execution context from configuration:

```scala
@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[slick.jdbc.JdbcBackend.Database] {

  private val db = slick.jdbc.JdbcBackend.Database.forConfig("myapp.database", config)

  override def get(): slick.jdbc.JdbcBackend.Database = db
}

@Singleton
class SlickUserDAOExecutionContextProvider @Inject() (actorSystem: akka.actor.ActorSystem) extends Provider[UserDAOExecutionContext] {
  private val instance = {
    val ec = actorSystem.dispatchers.lookup("myapp.database-dispatcher")
    new SlickUserDAOExecutionContext(ec)
  }

  override def get() = instance
}

class SlickUserDAOExecutionContext(ec: ExecutionContext) extends UserDAOExecutionContext {
  override def execute(runnable: Runnable): Unit = ec.execute(runnable)

  override def reportFailure(cause: Throwable): Unit = ec.reportFailure(cause)
}
```

The DAO must be closed to release JDBC connections, and this is handled through `UserDAOCloseHook`:

```scala
class UserDAOCloseHook @Inject()(dao: UserDAO, lifecycle: ApplicationLifecycle) {
  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Now closing database connections!")
      dao.close()
    }
  }
}
```

From there, the controller code is simple:

```scala
@Singleton
class HomeController @Inject() (userDAO: UserDAO, userDAOExecutionContext: UserDAOExecutionContext) extends Controller {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  implicit val ec = userDAOExecutionContext

  def index = Action.async {
    logger.info("Calling index")
    userDAO.all.map { users =>
      logger.info(s"Calling index: users = ${users}")
      Ok(views.html.index(users))
    }
  }
}
```

## Running

To run the project, start up Play:

```
project play
run
```

And that's it!  Now when you go to http://localhost:9000, you will see the list of users in the database.
