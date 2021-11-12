package controllers.api.v1

import com.google.inject._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

class UserLogoutController @Inject()(
    silhouette: Silhouette[DefaultEnv],
    cc: ControllerComponents
) extends AbstractController(cc) {

  def submit =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        silhouette.env.authenticatorService.discard(
          request.authenticator,
          Ok(Json.obj("message" -> "Signed out"))
        )
    })
}
