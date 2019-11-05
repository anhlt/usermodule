package db.migration.default

package db.migration.default

import slick.jdbc.MySQLProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import scala.concurrent.duration._
import scala.concurrent.Await



class V2_1__scala_addon extends BaseJavaMigration {

  lazy val db = Database.forConfig("db.default")

  class TestTable(tag: Tag) extends Table[(Int, Int)](tag, "testtable2") {
    val col1 = column[Int]("col1")
    val col2 = column[Int]("col2")
    def * = (col1, col2)
  }
  val testTable = TableQuery[TestTable]

  implicit val dialect: MySQLDialect = new MySQLDialect

  val m1 = TableMigration(testTable).create
    .addColumns(_.col1, _.col2)

  def migrate(context: Context): Unit = {
    db.run(m1())
  }

}
