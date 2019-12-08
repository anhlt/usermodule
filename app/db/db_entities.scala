package db
import org.joda.time.DateTime
import db.base.Entity
import java.util.UUID

case class DBUser(
    id: UUID,
    email: String,
    activated: Boolean = false,
    updatedAt: DateTime = new DateTime(),
    createdAt: DateTime = new DateTime()
) extends Entity

case class AuthToken(token: UUID, userID: UUID, expiry: DateTime)

case class DBOAuthAccessToken(
    id: UUID,
    accountId: Long,
    oauthClientId: Long,
    accessToken: String,
    refreshToken: String,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthAuthorizationCode(
    id: UUID,
    accountId: Long,
    oauthClientId: Long,
    code: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthClient(
    id: UUID,
    ownerId: Long,
    grantType: String,
    clientId: String,
    clientSecret: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

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
