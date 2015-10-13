## Play 2.4 with Slick 3.1

This is an example project that shows Play 2.4 working with a self contained Slick 3.1.0 module.  It uses HikariCP with PostgreSQL 9.4.


```
sudo su - postgres
createdb -U postgres myapp
```

Then check that "modules/slick/build.sbt" has the URL that you want, and type:

```
sbt flywayMigrate
```

This creates the tables for the Slick module.  Please see the [flyways documentation](http://flywaydb.org/getstarted/firststeps/sbt.html) for more details.



Note that the Slick module is self-contained and does not have any Play references.  Play JSON can be used standalone.  

The module is defined with a public API in "modules/api":

```
package com.example.user

trait UserDAO {

  def lookup(id: String): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user:User)

  def delete(id:String)

  def create(user:User): Future[Int]

}
```

and the main Play application uses the exposed API without touching the database implementation, using Guice DI.

There is a drawback to this model: when used in development mode, 

```
class DatabaseProvider @Inject() (dao: UserDAO, lifecycle: ApplicationLifecycle) extends Provider[] {
  lifecycle.addStopHook { () =>
    Future.successful {
      dao.close()
    }
  }
}
```
