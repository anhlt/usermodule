package db.migration.default

import db.base.CustomMySqlProfile
import db.base.CustomMySqlProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import db.base.{DBConfiguration, TableDefinition, Entity}

import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.reflect.ClassTag
import scala.concurrent.ExecutionContext

import db.{
  DBUser,
  DBOauthClient,
  DBOAuthAccessToken,
  DBOauthAuthorizationCode,
  DBLoginInfo,
  DBUserLoginInfo,
  DBPasswordInfo,
  DBOAuth1Info,
  DBOAuth2Info,
  AuthToken,
  DBUserRoles
}

import org.joda.time._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.UUID

class V1_5__add_user_fields extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomMySqlProfile)
  lazy val db = Database.forConfig("db.default")

  class UserTable(tag: Tag) extends Table[(UUID, String, Option[String], Option[String], Boolean, DateTime, DateTime)](tag, "users") {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val email = column[String]("email")
    val username = column[String]("username")
    val nickname = column[String]("nickname")
    val activated = column[Boolean]("activated")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
    def * =
      (
        id,
        email,
        username.?,
        nickname.?,
        activated,
        createdAt,
        updatedAt
      )
  }

  val userTable = TableQuery[UserTable]

  val m1 = TableMigration(userTable).addColumns(
    _.username,
    _.nickname,
  )

  def migrate(context: Context): Unit = {
    implicit val ec = ExecutionContext.global

    val actions = (for {
      _ <- m1()

    } yield ()).transactionally

    val rs = db.run(actions)
    Await.result(rs, 10 seconds)

  }

}
