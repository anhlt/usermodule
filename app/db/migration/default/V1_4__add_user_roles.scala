package db.migration.default

import com.github.tototoshi.slick.PostgresJodaSupport._
import db.DBUserRoles
import db.base.CustomProfile
import db.base.CustomProfile.api._
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import org.joda.time._
import slick.jdbc.JdbcProfile
import slick.migration.api._

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._

class V1_4__add_user_roles extends BaseJavaMigration {
  implicit val dialect: Dialect[_ <: JdbcProfile] = GenericDialect(CustomProfile)
  lazy val db: CustomProfile.backend.Database = Database.forConfig("db.default")

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
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    val actions = (for {
      _ <- m1()

    } yield ()).transactionally

    val rs = db.run(actions)
    Await.result(rs, 10 seconds)

  }

}
