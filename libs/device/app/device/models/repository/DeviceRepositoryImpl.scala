package device.models.repository
import device.models.entities.Device

import java.util.UUID
import models.entities.User
import utils.response.PagingResponse

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

    val allUserDevice =
      slickDeviceOwner.filter(_.userID === user.id).map(_.deviceInstanceID)

    val query = for {
      instance <- slickDeviceInstance.filter(_.id in allUserDevice)
      owner <- slickDeviceOwner if instance.id === owner.deviceInstanceID
      model <- slickDeviceModel if instance.model === model.id
    } yield (instance, owner, model)

    val values = db
      .run(for {
        records <- query.result
        count <- query.length.result

      } yield (records, count))
      .map({
        case (records, count) =>
          PagingResponse(
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
              .toList,
            count,
            count - (1 + 1) > 0
          )

      })

    println(values)

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
        } if (instance.id in allUserDevice)

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

  def allDevice(
      user: User,
      limit: Int,
      offset: Int
  ): Future[PagingResponse[Device.DeviceInstance]] = {
    val allUserDevice =
      slickDeviceOwner.filter(_.userID === user.id).map(_.deviceInstanceID)

    val query = for {
      instance <- slickDeviceInstance.filter(_.id in allUserDevice)
      owner <- slickDeviceOwner if instance.id === owner.deviceInstanceID
      model <- slickDeviceModel if instance.model === model.id
    } yield (instance, owner, model)

    db.run(for {
        records <- query.drop(offset).take(limit).result
        count <- query.length.result

      } yield (records, count))
      .map({
        case (records, count) =>
          PagingResponse(
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
              .toList,
            count,
            count - (offset + limit) > 0
          )

      })
  }

  override def deregisterDevice(user: User, deviceID: UUID): Future[Unit] = ???

}
