package models

import java.sql.Timestamp
import java.util.Date

import org.scalatestplus.play.PlaySpec
import play.test.WithApplication
import testhelpers.Injector

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class ModelScalaTestSpec extends PlaySpec {

  val taskRepo = Injector.inject[TaskRepo]

  "A task " should {
    "contains few todo tasks" in new WithApplication() {
      val action = taskRepo.getAllTask
      val result = Await.result(action, Duration.Inf)
      result.size > 0 mustBe true
    }

    "be inserted properly in the task list" in new WithApplication() {
      val action = taskRepo.addTask(new Task(1, "Test Task2", false, new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()))).flatMap(_ => taskRepo.getAllTask)
      val result = Await.result(action, Duration.Inf)
      result.filter(r => r.name equals "Test Task2").size mustBe 1
    }
  }
}
