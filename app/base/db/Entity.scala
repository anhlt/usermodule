package base.db

import org.joda.time.DateTime

trait Entity {
  def id: Option[Long]

  def createdAt: DateTime

  def updatedAt: DateTime

}
