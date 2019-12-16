package controllers.v1
import com.google.inject._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import utils.auth.DefaultEnv
import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.entities.User
import utils.auth.Role
import play.api.libs.json._
import utils.response.JsonWriters._
import utils.response._

class Users @Inject()(
    silhouette: Silhouette[DefaultEnv],
    cc: ControllerComponents
) extends AbstractController(cc) {

  def user =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        val user: User = request.identity
        Future.successful(Ok(Json.toJson(InstanceRespone(user))))
    })
}
