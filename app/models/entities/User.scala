package models.entities
import java.util.UUID
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import utils.auth.Role

case class User(
    id: UUID,
    loginInfo: LoginInfo,
    email: Option[String],
    nickname: Option[String],
    username: Option[String],
    activated: Boolean,
    roles: Seq[Role.Value] = Seq.empty[Role.Value]
) extends Identity
