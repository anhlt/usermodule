package base.db

import slick.jdbc.JdbcProfile

trait DBConfiguration {

  val driver: JdbcProfile

  import driver.api._

  lazy val db: Database = Database.forConfig("db.default")
}