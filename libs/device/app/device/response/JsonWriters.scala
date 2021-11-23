package device.response

import play.api.libs.json._
import device.models.entities.Device

object JsonWriters {
  implicit val deviceModelWriter = Json.writes[Device.DeviceModel]
  implicit val deviceInstanceWriter = Json.writes[Device.DeviceInstance]

}
