package controllers
import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import com.google.inject._
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.TokenEndpoint
import models.services.{MyTokenEndpoint, Oauth2DataHandler}
import scala.concurrent.ExecutionContext

class OAuth2Controller @Inject()(
    components: ControllerComponents,
    oauth2DataHandler: Oauth2DataHandler,
    implicit val exc: ExecutionContext
) extends AbstractController(components)
    with OAuth2Provider {

  override val tokenEndpoint: TokenEndpoint = MyTokenEndpoint
  def accessToken = Action.async { implicit request =>
    tokenEndpoint.handleRequest(request, oauth2DataHandler).map {
      case Left(e) =>
        new Status(e.statusCode)(responseOAuthErrorJson(e))
          .withHeaders(responseOAuthErrorHeader(e))
      case Right(r) =>
        Ok(Json.toJson(responseAccessToken(r)))
          .withHeaders("Cache-Control" -> "no-store", "Pragma" -> "no-cache")
    }
  }
}
