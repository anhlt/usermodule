package models.entities
import java.util.UUID
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

case class User(
    id: UUID,
    loginInfo: LoginInfo,
    email: Option[String],
    activated: Boolean
) extends Identity
