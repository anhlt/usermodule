package controllers.api.v1

import com.google.inject._
import com.mohiva.play.silhouette.api._
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
import play.api.libs.json._
import utils.response.JsonWriters._
import utils.response._
import java.util.UUID
import models.repositories.{
  OauthClientRepository,
  OauthAuthorizationCodeRepository
}
import scala.concurrent.ExecutionContext
import play.api.Logging
import forms.ClientAuthorizeForm
import forms.BaseForm._
import play.api.libs.json.{JsValue, Json, Writes}
import models.entities.User
import java.net.URI
// client_id=773
// redirect_uri=http:%2F%2Flocalhost%2Fmendeley%2Fserver_sample.php
// response_type=code
// scope=all
// state=213653957730.97845

class AuthorizationController @Inject()(
    silhouette: Silhouette[DefaultEnv],
    cc: ControllerComponents,
    oauthClientRepository: OauthClientRepository,
    oauthAuthorizationCodeRepository: OauthAuthorizationCodeRepository,
    implicit val exc: ExecutionContext
) extends AbstractController(cc)
    with Logging {

  def clientInfo(
      client_id: String,
      redirect_uri: String,
      response_type: String,
      state: Option[String]
  ) =
    silhouette.SecuredAction.async({
      implicit request: SecuredRequest[
        DefaultEnv,
        play.api.mvc.AnyContent
      ] =>
        oauthClientRepository
          .findByIdAndRedirectUri(client_id, redirect_uri)
          .map({ maybeClient =>
            maybeClient match {
              case Some(client) =>
                Ok(
                  Json.toJson(
                    InstanceRespone(
                      OauthClientResponse(
                        client.clientName,
                        client.clientDescription
                      )
                    )
                  )
                )

              case None =>
                NotFound(
                  Json
                    .obj("message" -> "Not Found")
                )
            }

          })
    })

  def uri(url: String, code: String, state: Option[String]): URI = {
    val enc = (p: String) => java.net.URLEncoder.encode(p, "utf-8")
    new java.net.URI(
      if (state.isDefined) s"${url}?code=${code}&state=${state.get}"
      else s"${url}?code=${code}"
    )
  }

  def authorize =
    silhouette.SecuredAction.async {
      implicit request: SecuredRequest[DefaultEnv, play.api.mvc.AnyContent] =>
        ClientAuthorizeForm.form
          .bindFromRequest()(request)
          .fold(
            formWithError =>
              Future.successful(BadRequest(Json.toJson(formWithError.errors))),
            data => {
              val currentUser: User = request.identity

              oauthClientRepository
                .findByIdAndRedirectUri(
                  data.client_id,
                  data.redirect_uri.getOrElse("")
                )
                .flatMap({
                  case Some(client) => {
                    oauthAuthorizationCodeRepository
                      .create(currentUser, client.id, data.redirect_uri)
                      .map(
                        code =>
                          Ok(
                            Json.toJson(
                              InstanceRespone(
                                OauthAuthorizationCodeResponse(
                                  code.code,
                                  uri(
                                    code.redirectUri
                                      .getOrElse(""),
                                    code.code,
                                    data.state
                                  ).toString
                                )
                              )
                            )
                          )
                      )
                  }
                  case None =>
                    Future.successful(
                      NotFound(
                        Json
                          .obj("message" -> "Not Found")
                      )
                    )
                })

            }
          )

    }

}
