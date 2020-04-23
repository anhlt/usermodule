package db
import org.joda.time.DateTime
import db.base.Entity
import java.util.UUID

case class DBUser(
    id: UUID,
    email: String,
    username: Option[String],
    nickname: Option[String],
    activated: Boolean = false,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBUserRoles(
    userId: UUID,
    role: String,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class AuthToken(token: UUID, userID: UUID, expiry: DateTime)

case class DBLoginInfo(
    id: UUID,
    providerID: String,
    providerKey: String,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBUserLoginInfo(
    userID: UUID,
    loginInfoId: UUID,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: UUID,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class DBOAuth1Info(
    id: UUID,
    token: String,
    secret: String,
    loginInfoId: UUID,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOAuth2Info(
    id: UUID,
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: UUID,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthClient(
    id: UUID,
    ownerId: UUID,
    grantType: String,
    clientId: String,
    clientSecret: String,
    redirectUri: Option[String],
    clientName: String,
    clientDescription: String,
    createdAt: DateTime
)

case class DBOauthAuthorizationCode(
    id: UUID,
    userId: UUID,
    oauthClientId: UUID,
    code: String,
    redirectUri: Option[String],
    createdAt: DateTime
)

case class DBOauthAccessToken(
    id: UUID,
    userId: UUID,
    oauthClientId: UUID,
    accessToken: String,
    refreshToken: String,
    createdAt: DateTime
)
