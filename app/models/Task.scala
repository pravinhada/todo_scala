package models

import java.sql.Timestamp

case class Task(id: Long, name: String, finished: Boolean, createdAt: Timestamp, updatedAt: Timestamp)
