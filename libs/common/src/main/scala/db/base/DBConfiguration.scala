package db.base

import slick.jdbc.JdbcProfile

trait DBConfiguration {

  val driver: JdbcProfile

  import driver.api._

  val db: Database
}


