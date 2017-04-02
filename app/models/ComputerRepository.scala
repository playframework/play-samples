package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class Computer(id: Option[Long] = None,
                    name: String,
                    introduced: Option[Date],
                    discontinued: Option[Date],
                    companyId: Option[Long])

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}


@javax.inject.Singleton
class ComputerRepository @Inject()(dbapi: DBApi, companyRepository: CompanyRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  // -- Parsers

  /**
   * Parse a Computer from a ResultSet
   */
  private val simple = {
    get[Option[Long]]("computer.id") ~
      get[String]("computer.name") ~
      get[Option[Date]]("computer.introduced") ~
      get[Option[Date]]("computer.discontinued") ~
      get[Option[Long]]("computer.company_id") map {
      case id~name~introduced~discontinued~companyId =>
        Computer(id, name, introduced, discontinued, companyId)
    }
  }

  /**
   * Parse a (Computer,Company) from a ResultSet
   */
  private val withCompany = simple ~ (companyRepository.simple ?) map {
    case computer~company => (computer,company)
  }

  // -- Queries

  /**
   * Retrieve a computer from the id.
   */
  def findById(id: Long): Future[Option[Computer]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from computer where id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }(ec)

  /**
   * Return a page of (Computer,Company).
   *
   * @param page Page to display
   * @param pageSize Number of computers per page
   * @param orderBy Computer property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[Page[(Computer, Option[Company])]] = Future {

    val offest = pageSize * page

    db.withConnection { implicit connection =>

      val computers = SQL(
        """
          select * from computer
          left join company on computer.company_id = company.id
          where computer.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize,
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(withCompany *)

      val totalRows = SQL(
        """
          select count(*) from computer
          left join company on computer.company_id = company.id
          where computer.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(computers, page, offest, totalRows)

    }

  }(ec)

  /**
   * Update a computer.
   *
   * @param id The computer id
   * @param computer The computer values.
   */
  def update(id: Long, computer: Computer) = Future {
    db.withConnection { implicit connection =>
      SQL(
        """
          update computer
          set name = {name}, introduced = {introduced}, discontinued = {discontinued}, company_id = {company_id}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> computer.name,
        'introduced -> computer.introduced,
        'discontinued -> computer.discontinued,
        'company_id -> computer.companyId
      ).executeUpdate()
    }
  }(ec)

  /**
   * Insert a new computer.
   *
   * @param computer The computer values.
   */
  def insert(computer: Computer) = Future {
    db.withConnection { implicit connection =>
      SQL(
        """
          insert into computer values (
            (select next value for computer_seq),
            {name}, {introduced}, {discontinued}, {company_id}
          )
        """
      ).on(
        'name -> computer.name,
        'introduced -> computer.introduced,
        'discontinued -> computer.discontinued,
        'company_id -> computer.companyId
      ).executeUpdate()
    }
  }(ec)

  /**
   * Delete a computer.
   *
   * @param id Id of the computer to delete.
   */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL("delete from computer where id = {id}").on('id -> id).executeUpdate()
    }
  }(ec)

}
