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
  AuthToken
}

import org.joda.time._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.UUID

class V1_3__add_tokenauth extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomMySqlProfile)
  lazy val db = Database.forConfig("db.default")


  class AuthTokens(tag: Tag) extends Table[AuthToken](tag, "auth_token") {

    def token = column[UUID]("token", O.PrimaryKey, O.SqlType("varchar(255)"))
    def userId = column[UUID]("user_id", O.SqlType("varchar(255)"))
    def expiry = column[DateTime]("expiry")
    def * = (token, userId, expiry) <> (AuthToken.tupled, AuthToken.unapply)
  }

  class UserTable(tag: Tag) extends Table[(java.util.UUID, String, Boolean, org.joda.time.DateTime, org.joda.time.DateTime)](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))

    val email = column[String]("email")
    val activated = column[Boolean]("activated")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()"))

    def * =
      (
        id,
        email,
        activated,
        createdAt,
        updatedAt
      ) 
  }

  val authTokenTable = TableQuery[AuthTokens]
  val usertable = TableQuery[UserTable]

  val m1 = TableMigration(authTokenTable).create.addColumns(
    _.token,
    _.userId,
    _.expiry
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
