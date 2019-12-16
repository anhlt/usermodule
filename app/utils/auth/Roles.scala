package utils.auth
import com.mohiva.play.silhouette.api.Authorization
import models.entities.User
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.i18n._
import play.api.mvc.Request
import scala.concurrent.Future

case class WithRole(role: Role) extends Authorization[User, JWTAuthenticator] {
  def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(
      implicit request: Request[B]
  ): Future[Boolean] = {
    Future.successful(true)
  }

}
import play.api.libs.json._

object Role {

  implicit val childAFormat = Json.format[Admin]
  implicit val childBFormat = Json.format[OrdinaryUser]
  implicit val familyFormat = Json.format[Role]
}
sealed trait Role {
  val name: String
}

case class Admin(name: String = "Admin") extends Role {}

case class OrdinaryUser(name: String = "OrdinaryUser") extends Role {}
