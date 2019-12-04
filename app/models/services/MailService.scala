package models.services

import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
import com.google.inject._

trait MailService {
  def sendResetPasswordEmail(email: String, url: String): Unit
  def sendActivateAccountEmail(email: String, url: String): Unit
}

class DumpMailService @Inject()(implicit ec: ExecutionContext)
    extends Logging
    with MailService {

  def sendResetPasswordEmail(email: String, url: String): Unit = {
    logger.info(s"Reset URL: $email: $url")
  }

  def sendActivateAccountEmail(email: String, url: String): Unit = {
    logger.info(s"Activate URL: $email: $url")
  }
}
