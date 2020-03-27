package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * The form which handles the sign up process.
  */
object SignUpForm {

  /**
    * A play framework form.
    */
  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "username" -> nonEmptyText,
      "nickname" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
    * The form data.
    *
    * @param firstName The first name of a user.
    * @param lastName The last name of a user.
    * @param email The email of the user.
    * @param password The password of the user.
    */
  case class Data(
      email: String,
      password: String,
      username: String,
      nickname: String
  )
}
