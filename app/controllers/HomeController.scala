package controllers

import services.{AuthTokenService, MailService, UserService}
import socket.IotSocketActor
import com.google.inject._
import play.api._
import play.api.mvc._
import authenticate.TokenAuthenticateAction
import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import play.api.i18n.I18nSupport
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}

import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(
    implicit system: ActorSystem,
    mat: Materializer,
    tokenAuthenticateAction: TokenAuthenticateAction,
    cc: ControllerComponents,
    silhouette: Silhouette[DefaultEnv],
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    authTokenService: AuthTokenService,
    passwordHasherRegistry: PasswordHasherRegistry

) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = tokenAuthenticateAction {
    implicit request: Request[AnyContent] =>
      val test = Set.empty[Int]
      Ok(views.html.index())
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef({ out =>
      IotSocketActor.props(out)
    })
  }
}
