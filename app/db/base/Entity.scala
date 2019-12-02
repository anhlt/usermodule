package db.base

import org.joda.time.DateTime

trait Entity {
  def id: Option[Long]

  def createdAt: DateTime

  def updatedAt: DateTime

}
