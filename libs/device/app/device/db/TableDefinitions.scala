package device.db
import com.github.tototoshi.slick.GenericJodaSupport
import db.base.{DBConfiguration, TableDefinition}

import org.joda.time._

import com.google.inject._
import java.util.UUID

class TableDefinitions @Inject()(
    override val dbConfiguration: DBConfiguration
) extends TableDefinition {
  object PortableJodaSupport extends GenericJodaSupport(dbConfiguration.driver)

  import dbConfiguration.driver.api._
  import PortableJodaSupport._

  class DeviceModelTable(tag: Tag)
      extends BaseTable[DBDeviceModel](tag, "device_models") {
    val desc = column[String]("desc")
    val name = column[String]("name")

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
      extends BaseTable[DBDeviceInstance](tag, "device_instances") {

    val model = column[UUID]("model")

    def * =
      (
        id,
        model,
        createdAt,
        updatedAt
      ) <> (DBDeviceInstance.tupled, DBDeviceInstance.unapply)

  }

  class DeviceOwnerTable(tag: Tag)
      extends BaseTable[DBDeviceOwner](tag, "device_owner") {

    val userID = column[UUID]("user_id")
    val deviceNote = column[String]("device_note")
    val deviceInstanceID = column[UUID]("device_instance_id")

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

  val slickDeviceModel = TableQuery[DeviceModelTable]
  val slickDeviceInstance = TableQuery[DeviceInstanceTable]
  val slickDeviceOwner = TableQuery[DeviceOwnerTable]

}
