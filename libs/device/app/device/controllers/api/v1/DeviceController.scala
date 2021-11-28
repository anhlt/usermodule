package device.controllers.api.v1
import com.google.inject._
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import device.forms.DeviceRegisterForm
import device.models.repository.DeviceRepository
import device.response.JsonWriters._
import forms.BaseForm._
import models.entities.User
import play.api.libs.json._
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}
import utils.auth.DefaultEnv
import utils.response.JsonWriters._
import utils.response._

import scala.concurrent.{ExecutionContext, Future}

class DeviceController @Inject()(
    @Named("DeviceSilhouetteProvider") silhouette: Silhouette[DefaultEnv],
    deviceRepository: DeviceRepository,
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def registerDevice: Action[AnyContent] =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        DeviceRegisterForm.form
          .bindFromRequest()
          .fold(
            formWithError =>
              Future.successful(BadRequest(Json.toJson(formWithError.errors))),
            data => {
              val user: User = request.identity
              (for {
                device <- deviceRepository
                  .registerDevice(
                    user,
                    data.modelID,
                    data.instanceID,
                    data.note
                  )
              } yield ({

                Ok(Json.toJson(device))

              }))

            }
          )

    })

  def devices(p: params.binders.Pager): Action[AnyContent] =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        val user: User = request.identity
        val devicesFuture =
              deviceRepository.allDevice(user, p.limit, p.offset)
        devicesFuture.map(
          devices => Ok(Json.toJson(devices))
        )

    })
}
