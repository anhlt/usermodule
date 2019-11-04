package authenticate
import play.api.mvc.{ActionBuilder, ActionBuilderImpl, Request, BodyParsers}

import com.google.inject._
import scala.concurrent.ExecutionContext
import play.api.mvc.Result
import scala.concurrent.Future
import play.api.Logging

class TokenAuthenticateAction @Inject()(parser: BodyParsers.Default)(
    implicit ec: ExecutionContext
) extends ActionBuilderImpl(parser) with  Logging {

  override def invokeBlock[A](
      request: Request[A],
      block: Request[A] => Future[Result]
  ): Future[Result] = {
    logger.info("TokenAuthenticate")
    block(request)
  }

}
