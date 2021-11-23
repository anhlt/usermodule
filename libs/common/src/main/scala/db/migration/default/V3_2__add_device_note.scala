package db.migration.default

import com.github.tototoshi.slick.PostgresJodaSupport._
import db.base.CustomProfile
import db.base.CustomProfile.api._
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import org.joda.time._
import slick.jdbc.JdbcProfile
import slick.migration.api._

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._
import db.base.Entity

class V3_2__add_device_note extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomProfile)
  lazy val db = Database.forConfig("db.default")

  case class DBDeviceOwner(
      id: UUID,
      userID: UUID,
      deviceInstanceID: UUID,
      deviceNote: String,
      updatedAt: DateTime = new DateTime(),
      createdAt: DateTime = new DateTime()
  ) extends Entity

  class DeviceOwnerTable(tag: Tag)
      extends Table[DBDeviceOwner](tag, "device_owner") {

    val id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val userID = column[UUID]("user_id")
    val deviceInstanceID = column[UUID]("device_instance_id")
    val deviceNote = column[String]("device_note", O.SqlType("text"))
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )

    def * =
      (
        id,
        userID,
        deviceInstanceID,
        deviceNote,
        createdAt,
        updatedAt
      ) <> (DBDeviceOwner.tupled, DBDeviceOwner.unapply)

  }

  val deviceOwnerTable = TableQuery[DeviceOwnerTable]

  val m3 = TableMigration(deviceOwnerTable).addColumns(
    _.deviceNote
  )

  def migrate(context: Context): Unit = {
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    val actions = (for {
      _ <- m3()
    } yield ()).transactionally

    Await.result(db.run(actions), 10 seconds)
  }

}
