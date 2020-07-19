package services
import play.api.libs.mailer._
import com.google.inject._
import play.api.Logging

class SMTPMailService @Inject()(mailerClient: MailerClient)
    extends MailService
    with Logging {

  def sendResetPasswordEmail(email: String, url: String): Unit = {
    val exemail = Email(
      "Reset Password Email",
      "tuananh.kirimaru@gmail.com",
      Seq(s"{email}"),
      // adds attachment
      // sends text, HTML or both...
      bodyText = Some(s"A text message $url"),
      bodyHtml = Some(
        s"""<html><body><p>An <b>html</b> message with cid <a href="$url">Reset Password</a></p></body></html>"""
      )
    )
    logger.info(s"Reset URL: $email: $url")
    mailerClient.send(exemail)
  }

  def sendActivateAccountEmail(email: String, url: String): Unit = {
    val exemail = Email(
      "Activation Email",
      "tuananh.kirimaru@gmail.com",
      Seq(s"${email}"),
      // adds attachment
      // sends text, HTML or both...
      bodyText = Some(s"A text message $url"),
      bodyHtml = Some(
        s"""<html><body><p>An <b>html</b> message with cid <a href="$url">Activation</a></p></body></html>"""
      )
    )
    logger.info(s"Activation URL: $email: $url")
    mailerClient.send(exemail)
  }
}
