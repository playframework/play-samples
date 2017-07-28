package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import views._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Manage a database of computers
  */
class HomeController @Inject()(computerService: ComputerRepository,
                               companyService: CompanyRepository,
                               cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  /**
    * This result directly redirect to the application home.
    */
  val Home = Redirect(routes.HomeController.list(0, 2, ""))

  /**
    * Describe the computer form (used in both edit and create screens).
    */
  val computerForm = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "name" -> nonEmptyText,
      "introduced" -> optional(date("yyyy-MM-dd")),
      "discontinued" -> optional(date("yyyy-MM-dd")),
      "company" -> optional(longNumber)
    )(Computer.apply)(Computer.unapply)
  )

  // -- Actions

  /**
    * Handle default path requests, redirect to computers list
    */
  def index = Action {
    Home
  }

  /**
    * Display the paginated list of computers.
    *
    * @param page    Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter  Filter applied on computer names
    */
  def list(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    computerService.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      Ok(html.list(page, orderBy, filter))
    }
  }

  /**
    * Display the 'edit form' of a existing Computer.
    *
    * @param id Id of the computer to edit
    */
  def edit(id: Long) = Action.async { implicit request =>
    computerService.findById(id).flatMap {
      case Some(computer) =>
        companyService.options.map { options =>
          Ok(html.editForm(id, computerForm.fill(computer), options))
        }
      case other =>
        Future.successful(NotFound)
    }
  }

  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the computer to edit
    */
  def update(id: Long) = Action.async { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => {
        logger.warn(s"form error: $formWithErrors")
        companyService.options.map { options =>
          BadRequest(html.editForm(id, formWithErrors, options))
        }
      },
      computer => {
        computerService.update(id, computer).map { _ =>
          Home.flashing("success" -> "Computer %s has been updated".format(computer.name))
        }
      }
    )
  }

  /**
    * Display the 'new computer form'.
    */
  def create = Action.async { implicit request =>
    companyService.options.map { options =>
      Ok(html.createForm(computerForm, options))
    }
  }

  /**
    * Handle the 'new computer form' submission.
    */
  def save = Action.async { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => companyService.options.map { options =>
        BadRequest(html.createForm(formWithErrors, options))
      },
      computer => {
        computerService.insert(computer).map { _ =>
          Home.flashing("success" -> "Computer %s has been created".format(computer.name))
        }
      }
    )
  }

  /**
    * Handle computer deletion.
    */
  def delete(id: Long) = Action.async {
    computerService.delete(id).map { _ =>
      Home.flashing("success" -> "Computer has been deleted")
    }
  }

}
            
