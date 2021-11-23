package device.models.repository

import device.models.entities.Device._
import models.entities.User
import device.models.entities.Device
import scala.concurrent.Future
import java.util.UUID

trait DeviceRepository {

  def allDevice(user: User): Future[List[DeviceInstance]]

  def registerDevice(
      user: User,
      deviceModelID: UUID,
      deviceInstanceID: UUID,
      note: String
  ): Future[DeviceInstance]

  def deregisterDevice(user: User, deviceID: UUID): Future[Unit]
}
