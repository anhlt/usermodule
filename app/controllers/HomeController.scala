package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import authenticate.TokenAuthenticateAction
import com.google.inject._
import controllers.socket.IotSocketActor
import play.api.libs.streams.ActorFlow
import services.{AuthTokenService, UserService}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import play.api.mvc._
import utils.auth.DefaultEnv
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
