package base.db

import java.sql.Timestamp

trait Entity {
  def id: Option[Long]

  def created: Timestamp

  def updated: Timestamp

}
