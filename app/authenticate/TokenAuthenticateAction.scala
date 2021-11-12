package authenticate
import com.google.inject._
import play.api.Logging
import play.api.mvc.{ActionBuilderImpl, BodyParsers, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

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
