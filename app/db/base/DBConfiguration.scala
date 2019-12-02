package db.base

import slick.jdbc.JdbcProfile
import com.github.tototoshi.slick.GenericJodaSupport

trait DBConfiguration {

  val driver: JdbcProfile

  import driver.api._

  lazy val db: Database = Database.forConfig("db.default")
}
