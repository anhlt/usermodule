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

class V1_4__add_user_role extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomMySqlProfile)
  lazy val db = Database.forConfig("db.default")

  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
  }

  class UserRoles(tag: Tag) extends Table[DBUserRoles](tag, "user_roles") {
    def userId = column[UUID]("user_id", O.SqlType("varchar(255)"))
    def role = column[String]("role", O.SqlType("varchar(255)"))
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
    def * =
      (userId, role, createdAt, updatedAt) <> (DBUserRoles.tupled, DBUserRoles.unapply)
  }

  val authTokenTable = TableQuery[UserRoles]

  val m1 = TableMigration(authTokenTable).create.addColumns(
    _.userId,
    _.role,
    _.createdAt,
    _.updatedAt
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
