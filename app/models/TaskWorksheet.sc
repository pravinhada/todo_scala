import java.sql.Timestamp
import java.util.Date

import models.Task

val task = Task(1, "Testing", false, new Timestamp(new Date().getTime),new Timestamp(new Date().getTime))

task toString

println(task.name)
