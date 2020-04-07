package forms
import play.api.data.Form
import play.api.data.Forms._

object ClientAuthorizeForm {

  /**
    * A play framework form.
    */
  val form = Form(
    mapping(
      "clientId" -> nonEmptyText,
      "redirectUri" -> optional(text),
      "responseType" -> nonEmptyText
    )(ClientAuthorizeData.apply)(ClientAuthorizeData.unapply)
  )

  /**
    * The form data.
    *
    * @param email The email of the user.
    * @param password The password of the user.
    * @param rememberMe Indicates if the user should stay logged in on the next visit.
    */
  case class ClientAuthorizeData(
      clientId: String,
      redirectUri: Option[String],
      responseType: String
  )
}
