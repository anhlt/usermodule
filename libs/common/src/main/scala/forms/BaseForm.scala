package forms
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.data.{ Form, FormError }
import play.api.i18n.Messages


object BaseForm {
  implicit object FormErrorWrites extends Writes[FormError] {
    override def writes(o: FormError): JsValue = Json.obj(
      "key" -> Json.toJson(o.key),
      "message" -> Json.toJson(o.messages)
    )
  }
}
