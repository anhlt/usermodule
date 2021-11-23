package device.models.repository
import device.models.entities.Device

import java.util.UUID
import models.entities.User

import scala.concurrent.Future
import device.db.{
  DBDeviceInstance,
  DBDeviceModel,
  DBDeviceOwner,
  TableDefinitions
}

import scala.concurrent.ExecutionContext
import com.google.inject.Inject

class DeviceRepositoryImpl @Inject()(
    val tableDefinations: TableDefinitions,
    implicit val ex: ExecutionContext
) extends DeviceRepository {

  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  override def registerDevice(
      user: User,
      deviceModelID: UUID,
      deviceInstanceID: UUID,
      note: String
  ): Future[Device.DeviceInstance] = {

    val retrieveDeviceModel =
      slickDeviceModel.filter(_.id === deviceModelID).result.headOption

    def deviceInstance(modelID: UUID) = slickDeviceInstance += DBDeviceInstance(
      deviceInstanceID,
      modelID
    )

    val insertInstance = for {
      model <- retrieveDeviceModel
      instance <- model
        .map(m => {
          deviceInstance(m.id)
        })
        .get

    } yield (model.get)

    val actions = (for {
      model <- insertInstance
      _ <- slickDeviceOwner += DBDeviceOwner(
        UUID.randomUUID(),
        user.id,
        deviceInstanceID,
        note
      )
    } yield (model)).transactionally

    db.run(actions)
      .map(
        model =>
          Device.DeviceInstance(
            deviceInstanceID,
            Device.DeviceModel(deviceModelID, model.name, model.desc),
            user.id,
            note
          )
      )

  }

  def allDevice(user: User): Future[List[Device.DeviceInstance]] = {

    val actions =
      (for {
        ((instance, owner), model) <- {
          ((slickDeviceInstance join slickDeviceOwner on {
            case (instanceTable, ownerTable) =>
              (ownerTable.deviceInstanceID === instanceTable.id)
          } join slickDeviceModel on {
            case ((instanceTable, ownerTable), modelTable) =>
              instanceTable.model === modelTable.id
          }))
        } if owner.userID === user.id

      } yield (instance, owner, model)).result

    db.run(actions)
      .map({ records =>
        records
          .map({
            case (i, o, m) =>
              Device.DeviceInstance(
                i.id,
                Device.DeviceModel(m.id, m.name, m.desc),
                o.id,
                o.deviceNote
              )

          })
          .toList

      })

  }

  override def deregisterDevice(user: User, deviceID: UUID): Future[Unit] = ???

}
