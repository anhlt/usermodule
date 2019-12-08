package db.migration.default

import slick.jdbc.MySQLProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import db.base.{DBConfiguration, TableDefinition, Entity}

import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.reflect.ClassTag

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
  AuthToken
}

import org.joda.time._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.UUID

class V1_3__add_tokenauth extends BaseJavaMigration {
  implicit val dialect: MySQLDialect = new MySQLDialect
  lazy val db = Database.forConfig("db.default")

  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[UUID]("id", O.PrimaryKey, O.AutoInc)
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
  }

  class AuthTokens(tag: Tag)
      extends Table[AuthToken](tag, "auth_token") {

    def token = column[UUID]("token", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def expiry = column[DateTime]("expiry")
    def * = (token, userId, expiry) <> (AuthToken.tupled, AuthToken.unapply)
  }

  class UserTable(tag: Tag) extends BaseTable[DBUser](tag, "users") {

    val email = column[String]("email")
    val activated = column[Boolean]("activated")

    def * =
      (
        id,
        email,
        activated,
        createdAt,
        updatedAt
      ) <> (DBUser.tupled, DBUser.unapply _)
  }

  val authTokenTable = TableQuery[AuthTokens]
  val usertable = TableQuery[UserTable]

  val m1 = TableMigration(authTokenTable).create.addColumns(
    _.token,
    _.userId,
    _.expiry
  )

  val m2 = TableMigration(usertable).addColumns(
    _.activated
  )

  def migrate(context: Context): Unit = {

    db.run(m1())
    db.run(m2())
  }

}
