package device.controllers.api.v1
import com.google.inject._
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.entities.User
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.auth.DefaultEnv
import utils.response.JsonWriters._
import utils.response._

import scala.concurrent.Future

class DeviceController @Inject()(
    @Named("DeviceSilhouetteProvider") silhouette: Silhouette[DefaultEnv],
    cc: ControllerComponents
) extends AbstractController(cc) {

  def user: Action[AnyContent] =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        val user: User = request.identity
        Future.successful(Ok(Json.toJson(InstanceRespone(user))))
    })
}
