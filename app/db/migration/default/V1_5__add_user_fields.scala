package db.migration.default

import com.github.tototoshi.slick.PostgresJodaSupport._
import db.base.CustomProfile
import db.base.CustomProfile.api._
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import org.joda.time._
import slick.migration.api._

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class V1_5__add_user_fields extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomProfile)
  lazy val db = Database.forConfig("db.default")

  class UserTable(tag: Tag)
      extends Table[
        (
            UUID,
            String,
            Option[String],
            Option[String],
            Boolean,
            DateTime,
            DateTime
        )
      ](tag, "users") {

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
    _.nickname
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
