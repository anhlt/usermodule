package db
import com.github.tototoshi.slick.GenericJodaSupport

import db.base.{DBConfiguration, TableDefinition}
import org.joda.time._
import com.mohiva.play.silhouette.api.LoginInfo
import com.google.inject._
import java.util.UUID

class TableDefinitions @Inject()(
    override val dbConfiguration: DBConfiguration
) extends TableDefinition {

  object PortableJodaSupport extends GenericJodaSupport(dbConfiguration.driver)
  import dbConfiguration.driver.api._
  import PortableJodaSupport._

  class UserTable(tag: Tag) extends BaseTable[DBUser](tag, "users") {

    val email = column[String]("email")
    val activated = column[Boolean]("activated")
    val username = column[String]("username")
    val nickname = column[String]("nickname")

    def * =
      (
        id,
        email,
        username.?,
        nickname.?,
        activated,
        createdAt,
        updatedAt
      ) <> (DBUser.tupled, DBUser.unapply _)
  }

  class AuthTokens(tag: Tag) extends Table[AuthToken](tag, "auth_token") {

    def token = column[UUID]("token", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def expiry = column[DateTime]("expiry")
    def * = (token, userId, expiry) <> (AuthToken.tupled, AuthToken.unapply)
  }

  class LoginInfos(tag: Tag) extends BaseTable[DBLoginInfo](tag, "logininfo") {
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * =
      (id, providerID, providerKey, createdAt, updatedAt) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  class UserLoginInfos(tag: Tag)
      extends BaseTable[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[UUID]("userID")
    def loginInfoId = column[UUID]("loginInfoId")
    def * =
      (userID, loginInfoId, createdAt, updatedAt) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  class PasswordInfos(tag: Tag)
      extends BaseTable[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[UUID]("loginInfoId")
    def * =
      (hasher, password, salt, loginInfoId, createdAt, updatedAt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  class OAuth1Infos(tag: Tag)
      extends BaseTable[DBOAuth1Info](tag, "oauth1info") {
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[UUID]("loginInfoId")
    def * =
      (id, token, secret, loginInfoId, createdAt, updatedAt) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  class OAuth2Infos(tag: Tag)
      extends BaseTable[DBOAuth2Info](tag, "oauth2info") {
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[UUID]("logininfoid")
    def * =
      (
        id,
        accessToken,
        tokenType,
        expiresIn,
        refreshToken,
        loginInfoId,
        createdAt,
        updatedAt
      ) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  class UserRoles(tag: Tag) extends Table[DBUserRoles](tag, "user_roles") {
    def userId = column[UUID]("user_id", O.SqlType("varchar(255)"))
    def role = column[String]("role", O.SqlType("varchar(255)"))
    val createdAt = column[DateTime](
      "created_date",
      O.Default(DateTime.now())
    )
    val updatedAt = column[DateTime](
      "updated_date",
      O.Default(DateTime.now())
    )
    def * =
      (userId, role, createdAt, updatedAt) <> (DBUserRoles.tupled, DBUserRoles.unapply)
  }

  class OauthClientTable(tag: Tag)
      extends Table[DBOauthClient](tag, "oauth_client") {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val ownerId = column[UUID]("owner_id", O.SqlType("varchar(255)"))
    val grantType = column[String]("grant_type")
    val clientId = column[String]("client_id")
    val clientSecret = column[String]("client_secret")
    val clientName = column[String]("client_name")
    val clientDescription =
      column[String]("client_description", O.SqlType("varchar(255)"))
    val redirectUri = column[String]("ridirect_uri")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))

    def * =
      (
        id,
        ownerId,
        grantType,
        clientId,
        clientSecret,
        redirectUri.?,
        clientName,
        clientDescription,
        createdAt
      ) <> (DBOauthClient.tupled, DBOauthClient.unapply)

  }

  class OauthAuthorizationCodeTable(tag: Tag)
      extends Table[DBOauthAuthorizationCode](
        tag,
        "oauth_authorization_code"
      ) {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val userId = column[UUID]("user_id", O.SqlType("varchar(255)"))
    val oauthClientId =
      column[UUID]("oauth_client_id", O.SqlType("varchar(255)"))
    val code = column[String]("code")
    val redirectUri = column[String]("ridirect_uri")

    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))

    def * =
      (
        id,
        userId,
        oauthClientId,
        code,
        redirectUri.?,
        createdAt
      ) <> (DBOauthAuthorizationCode.tupled, DBOauthAuthorizationCode.unapply)

  }

  class OauthAccessTokenTable(tag: Tag)
      extends Table[DBOauthAccessToken](
        tag,
        "oauth_access_token"
      ) {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val userId = column[UUID]("user_id", O.SqlType("varchar(255)"))
    val oauthClientId =
      column[UUID]("oauth_client_id", O.SqlType("varchar(255)"))
    val accessToken = column[String]("access_token")
    val refreshToken = column[String]("refesh_token")

    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))

    def * =
      (
        id,
        userId,
        oauthClientId,
        accessToken,
        refreshToken,
        createdAt
      ) <> (DBOauthAccessToken.tupled, DBOauthAccessToken.unapply)

  }

  // table query definitions
  val slickUsers = TableQuery[UserTable]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickAuthTokens = TableQuery[AuthTokens]
  val slickUserRoles = TableQuery[UserRoles]

  val slickSClients = TableQuery[OauthClientTable]
  val slickSAuthorizationCodes = TableQuery[OauthAuthorizationCodeTable]
  val slickSAccessTokenTable = TableQuery[OauthAccessTokenTable]

  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(
      dbLoginInfo =>
        dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey
    )
}
