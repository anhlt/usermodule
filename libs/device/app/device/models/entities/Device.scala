package device.models.entities

import java.util.UUID

object Device {

  case class DeviceModel(id: UUID, name: String, desc: String)
  case class DeviceInstance(
      id: UUID,
      model: DeviceModel,
      userID: UUID,
      note: String
  )

}