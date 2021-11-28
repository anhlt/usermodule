package device.models.repository

import device.models.entities.Device._
import models.entities.User
import device.models.entities.Device
import scala.concurrent.Future
import java.util.UUID
import utils.response.PagingResponse

trait DeviceRepository {

  def allDevice(user: User): Future[List[DeviceInstance]]
  def allDevice(user: User, limit: Int, offset: Int): Future[PagingResponse[DeviceInstance]]

  def registerDevice(
      user: User,
      deviceModelID: UUID,
      deviceInstanceID: UUID,
      note: String
  ): Future[DeviceInstance]

  def deregisterDevice(user: User, deviceID: UUID): Future[Unit]
}
