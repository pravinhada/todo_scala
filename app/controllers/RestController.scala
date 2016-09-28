package controllers

import java.sql.Timestamp
import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Task, TaskRepo}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RestController @Inject()(taskRepo: TaskRepo) extends Controller {

  implicit val taskFormat: Writes[Task] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "finished").write[Boolean] and
      (JsPath \ "createdAt").write[Timestamp] and
      (JsPath \ "updatedAt").write[Timestamp]
    )(unlift(Task.unapply))

  /**
    * Return the list in JSON format REST call
    */
  def listTasks(completed: Boolean) = Action.async {
    println("Returning only " + completed + " task")
    taskRepo.getAllTaskByStatus(completed) map(seq => Ok(Json.toJson(seq.toList)))
  }

  def task(taskId: Long) = Action.async {
    taskRepo.findTaskById(taskId).map(res => Ok(Json.toJson(res)))
  }

  def edit(taskId: Long) = Action.async {
    // TODO: need to edit this
    taskRepo.findTaskById(taskId).map(res => Ok(Json.toJson(res)))
  }
}
