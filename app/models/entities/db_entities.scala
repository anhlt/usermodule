package models.entities
import org.joda.time.DateTime
import base.db.Entity

case class DBAccount(
    id: Option[Long],
    email: String,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOAuthAccessToken(
    id: Option[Long],
    accountId: Long,
    oauthClientId: Long,
    accessToken: String,
    refreshToken: String,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthAuthorizationCode(
    id: Option[Long],
    accountId: Long,
    oauthClientId: Long,
    code: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOauthClient(
    id: Option[Long],
    ownerId: Long,
    grantType: String,
    clientId: String,
    clientSecret: String,
    redirectUri: Option[String],
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBLoginInfo(
    id: Option[Long],
    providerID: String,
    providerKey: String,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBUserLoginInfo(
    id: Option[Long],
    userID: String,
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBPasswordInfo(
    id: Option[Long],
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOAuth1Info(
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity

case class DBOAuth2Info(
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long,
    updatedAt: DateTime,
    createdAt: DateTime
) extends Entity
