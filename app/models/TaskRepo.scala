package models

import java.sql.Timestamp

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future

trait TaskRepo {
  def addTask(task: Task): Future[Long]
  def getAllTask: Future[Seq[Task]]
  def findTaskById(id: Long): Future[Task]
  def removeTask(id: Long)
}

class TaskRepoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends TaskRepo {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.driver.api._

  val tasks = TableQuery[Tasks]

  def addTask(task: Task): Future[Long] = db.run(tasks returning tasks.map(_.id) += task)

  def getAllTask: Future[Seq[Task]] = db.run(tasks.result)

  def findTaskById(taskId: Long): Future[Task] = db.run(tasks.filter(_.id === taskId).result.head)

  def removeTask(id: Long): Unit = {
    val query = tasks.filter(_.id === id)
    val action = query.delete
    db.run(action)
    println("Deleted Task with Id: " + id)
  }

  class Tasks(tag: Tag) extends Table[Task](tag, "TASKS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def finished = column[Boolean]("FINISHED")
    def createdAt = column[Timestamp]("CREATED_AT")
    def updatedAt = column[Timestamp]("UPDATED_AT")

    override def * = (id, name, finished, createdAt, updatedAt) <> (Task.tupled, Task.unapply)
  }
}