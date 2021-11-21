package db.migration.default

import com.github.tototoshi.slick.PostgresJodaSupport._
import db.base.CustomProfile
import db.base.CustomProfile.api._
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import org.joda.time._
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape
import slick.migration.api._

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._

class V1_3__add_tokenauth extends BaseJavaMigration {
  implicit val dialect: Dialect[_ <: JdbcProfile] = GenericDialect(
    CustomProfile
  )
  lazy val db: CustomProfile.backend.Database = Database.forConfig("db.default")

  class AuthTokens(tag: Tag)
      extends Table[(UUID, UUID, DateTime)](tag, "auth_token") {

    def token: Rep[UUID] =
      column[UUID]("token", O.PrimaryKey, O.SqlType("varchar(255)"))
    def userId: Rep[UUID] = column[UUID]("user_id", O.SqlType("varchar(255)"))
    def expiry: Rep[DateTime] = column[DateTime]("expiry")
    def * : ProvenShape[(UUID, UUID, DateTime)] = (token, userId, expiry)
  }

  class UserTable(tag: Tag)
      extends Table[
        (
            java.util.UUID,
            String,
            Boolean,
            org.joda.time.DateTime,
            org.joda.time.DateTime
        )
      ](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))

    val email = column[String]("email")
    val activated = column[Boolean]("activated")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt =
      column[DateTime]("updated_date", O.SqlType("timestamp default now()"))

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
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    val actions = (for {
      _ <- m1()

    } yield ()).transactionally

    val rs = db.run(actions)
    Await.result(rs, 10 seconds)

  }

}
