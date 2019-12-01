package db.migration.default

import slick.jdbc.MySQLProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import base.db.{DBConfiguration, TableDefinition, Entity}

import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.reflect.ClassTag

import models.entities.{
  DBAccount,
  DBOauthClient,
  DBOAuthAccessToken,
  DBOauthAuthorizationCode,
  DBLoginInfo,
  DBUserLoginInfo,
  DBPasswordInfo,
  DBOAuth1Info,
  DBOAuth2Info
}

import org.joda.time._

class V1_2__new extends BaseJavaMigration {
  implicit val dialect: MySQLDialect = new MySQLDialect
  lazy val db = Database.forConfig("db.default")

  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
  }

  class UserTable(tag: Tag) extends BaseTable[DBAccount](tag, "users") {

    val email = column[String]("email")

    def * =
      (
        id.?,
        email,
        createdAt,
        updatedAt
      ) <> (DBAccount.tupled, DBAccount.unapply _)
  }

  val testTable = TableQuery[UserTable]

  val m1 = TableMigration(testTable).create
    .addColumns(_.id, _.email, _.createdAt, _.updatedAt)

  def migrate(context: Context): Unit = {
    db.run(m1())
  }

}
