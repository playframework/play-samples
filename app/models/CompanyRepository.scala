package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class Company(id: Option[Long] = None, name: String)

@javax.inject.Singleton
class CompanyRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
   * Parse a Company from a ResultSet
   */
  private[models] val simple = {
    get[Option[Long]]("company.id") ~
      get[String]("company.name") map {
      case id~name => Company(id, name)
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Future[Seq[(String,String)]] = Future(db.withConnection { implicit connection =>
    SQL("select * from company order by name").as(simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
    }
  })(ec)

}


