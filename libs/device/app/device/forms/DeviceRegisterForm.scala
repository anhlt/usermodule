package device.forms

import java.util.UUID
import play.api.data.Form
import play.api.data.Forms._

object DeviceRegisterForm {

  val form = Form(
    mapping(
      "modelID" -> uuid,
      "instanceID" -> uuid,
      "note" -> text
    )(DeviceRegisterData.apply)(DeviceRegisterData.unapply)
  )

  case class DeviceRegisterData(
      modelID: UUID,
      instanceID: UUID,
      note: String
  )
}
