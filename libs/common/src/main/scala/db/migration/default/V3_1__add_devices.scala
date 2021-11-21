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

class V3_1__add_devices extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomProfile)
  lazy val db = Database.forConfig("db.default")

  case class DBDeviceModel(
      id: UUID,
      name: String,
      desc: String,
      updatedAt: DateTime = new DateTime(),
      createdAt: DateTime = new DateTime()
  ) extends Entity

  case class DBDeviceInstance(
      id: UUID,
      model: UUID,
      updatedAt: DateTime = new DateTime(),
      createdAt: DateTime = new DateTime()
  ) extends Entity

  case class DBDeviceOwner(
      id: UUID,
      userID: UUID,
      deviceInstanceID: UUID,
      updatedAt: DateTime = new DateTime(),
      createdAt: DateTime = new DateTime()
  ) extends Entity

  class DeviceModelTable(tag: Tag)
      extends Table[DBDeviceModel](tag, "device_models") {

    val id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val name = column[String]("name")
    val desc = column[String]("desc", O.SqlType("text"))
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )

    def * =
      (
        id,
        name,
        desc,
        createdAt,
        updatedAt
      ) <> (DBDeviceModel.tupled, DBDeviceModel.unapply)
  }

  class DeviceInstanceTable(tag: Tag)
      extends Table[DBDeviceInstance](tag, "device_instances") {

    val id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val model = column[UUID]("model")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )

    def * =
      (
        id,
        model,
        createdAt,
        updatedAt
      ) <> (DBDeviceInstance.tupled, DBDeviceInstance.unapply)

  }

  class DeviceOwnerTable(tag: Tag)
      extends Table[DBDeviceOwner](tag, "device_owner") {

    val id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val userID = column[UUID]("user_id")
    val deviceInstanceID = column[UUID]("device_instance_id")
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
        createdAt,
        updatedAt
      ) <> (DBDeviceOwner.tupled, DBDeviceOwner.unapply)

  }

  val deviceModelTable = TableQuery[DeviceModelTable]
  val deviceInstanceTable = TableQuery[DeviceInstanceTable]
  val deviceOwnerTable = TableQuery[DeviceOwnerTable]

  val m1 = TableMigration(deviceModelTable).create.addColumns(
    _.id,
    _.name,
    _.desc,
    _.createdAt,
    _.updatedAt
  )

  val m2 = TableMigration(deviceInstanceTable).create.addColumns(
    _.id,
    _.model,
    _.createdAt,
    _.updatedAt
  )
  val m3 = TableMigration(deviceOwnerTable).create.addColumns(
    _.id,
    _.userID,
    _.deviceInstanceID,
    _.createdAt,
    _.updatedAt
  )

  def migrate(context: Context): Unit = {
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    val actions = (for {
      _ <- m1()
      _ <- m2()
      _ <- m3()
      _ <- {
        deviceModelTable += DBDeviceModel(
          UUID.randomUUID(),
          "pump_001",
          "first device"
        )
      }
    } yield ()).transactionally

    Await.result(db.run(actions), 10 seconds)
  }

}
