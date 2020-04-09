package forms
import play.api.data.Form
import play.api.data.Forms._

object ClientAuthorizeForm {

  /**
    * A play framework form.
    */
  val form = Form(
    mapping(
      "client_id" -> nonEmptyText,
      "redirect_uri" -> optional(text),
      "response_type" -> nonEmptyText,
      "state" -> optional(text)
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
      client_id: String,
      redirect_uri: Option[String],
      response_type: String,
      state: Option[String]
  )
}
