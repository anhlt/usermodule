package utils.auth
import com.mohiva.play.silhouette.api.Authorization
import models.entities.User
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.i18n._
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.libs.json._

case class WithRole(role: Role.Value) extends Authorization[User, JWTAuthenticator] {
  def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(
      implicit request: Request[B]
  ): Future[Boolean] = {
    Future.successful(true)
  }

}


object Role extends Enumeration {

  type Role = Value

  val Admin = Value("Admin")
  val OrdinaryUser = Value("OrdinaryUser")

  implicit val myEnumReads = Reads.enumNameReads(Role)
  implicit val myEnumWrites = Writes.enumNameWrites
}