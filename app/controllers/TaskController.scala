package controllers

import java.sql.Timestamp
import java.util.Date
import javax.inject.Inject

import com.google.inject.Singleton
import models.{Task, TaskForm, TaskRepo}
import play.api.data.Form
import play.api.data.Forms.{boolean, mapping, nonEmptyText}
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TaskController @Inject() (val messagesApi: MessagesApi, taskRepo: TaskRepo) extends Controller with I18nSupport {

  private val taskForm: Form[TaskForm] = Form(mapping("name" -> nonEmptyText, "finished" -> boolean)(TaskForm.apply)(TaskForm.unapply))

  def index = Action {
    Redirect(routes.TaskController.list())
  }

  def newTask = Action { implicit  request =>
    val form = if (request.flash.get("error").isDefined) taskForm.bind(request.flash.data) else taskForm
    Ok(views.html.task(form))
  }

  def save = Action(parse.form(taskForm)) { implicit request =>
    val formData = taskForm.bindFromRequest()
    formData.fold(
      hasErrors = {form => Redirect(routes.TaskController.newTask())
      },
      success = { newTask =>
        val time = new Timestamp(new Date().getTime)
        val task = Task(1, newTask.name, newTask.finished, time, time)
        taskRepo.addTask(task)
      }
    )
    Redirect(routes.TaskController.list())
  }

  def list = Action.async { implicit  request =>
    taskRepo.getAllTask map(res => Ok(views.html.todos(res)))
  }

  def detail(taskId: Long) = Action.async { implicit request =>
    taskRepo.findTaskById(taskId).map(task => Ok(views.html.details(task)))
  }

  def remove(taskId: Long) = Action { implicit request =>
    taskRepo.removeTask(taskId)
    Redirect(routes.TaskController.list())
  }

  /**
    * Return the list in JSON format REST call
    */
  def listTasks = Action.async {
    implicit val taskFormat: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "finished").write[Boolean] and
      (JsPath \ "createdAt").write[Timestamp] and
      (JsPath \ "updatedAt").write[Timestamp]
      )(unlift(Task.unapply))
    taskRepo.getAllTask map(seq => Ok(Json.toJson(seq.toList)))
  }
}
