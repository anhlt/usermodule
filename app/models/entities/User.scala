package models.entities
import java.util.UUID
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import utils.auth.Role

case class User(
    id: UUID,
    loginInfo: LoginInfo,
    email: Option[String],
    activated: Boolean,
    roles: Seq[Role] = Seq.empty[Role]
) extends Identity
