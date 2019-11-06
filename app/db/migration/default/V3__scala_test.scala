package db.migration.default

import slick.jdbc.MySQLProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

class V3__scala_test extends BaseJavaMigration {

  lazy val db = Database.forConfig("db.default")

  class TestTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "testtable") {
    val col1 = column[Int]("col1")
    val col2 = column[Int]("col2")
    val col3 = column[Int]("col3")
    def * = (col1, col2, col3)
  }
  val testTable = TableQuery[TestTable]

  implicit val dialect: MySQLDialect = new MySQLDialect

  val m1 = TableMigration(testTable)
    .addColumns(_.col3)

  def migrate(context: Context): Unit = {
    db.run(m1())
  }

}
