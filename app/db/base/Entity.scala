package db.base

import org.joda.time.DateTime

trait Entity {

  def createdAt: DateTime

  def updatedAt: DateTime

}
