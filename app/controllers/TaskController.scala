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
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TaskController @Inject() (val messagesApi: MessagesApi, taskRepo: TaskRepo) extends Controller with I18nSupport {

  private val taskForm: Form[TaskForm] = Form(mapping("name" -> nonEmptyText, "finished" -> boolean)(TaskForm.apply)(TaskForm.unapply))

  def index = Action {
    Redirect(routes.TaskController.list())
  }

  def newTask = Action {
    Ok(views.html.task(taskForm))
  }

  def save = Action { implicit request =>
    val formData = taskForm.bindFromRequest()
    formData.fold(
      formWithErrors => Ok(views.html.task(formWithErrors)),
      value => {
        val time = new Timestamp(new Date().getTime)
        val task = Task(1, value.name, value.finished, time, time)
        taskRepo.addTask(task)
        Redirect(routes.TaskController.list())
      }
    )
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
}
