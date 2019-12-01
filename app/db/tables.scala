package db
import com.github.tototoshi.slick.GenericJodaSupport
import models.entities.{
  DBAccount,
  DBOauthClient,
  DBOAuthAccessToken,
  DBOauthAuthorizationCode,
  DBLoginInfo,
  DBUserLoginInfo,
  DBPasswordInfo,
  DBOAuth1Info,
  DBOAuth2Info
}

import base.db.{DBConfiguration, TableDefinition}
import org.joda.time._

class OauthTableDefinitions(
    override val dbConfiguration: DBConfiguration
) extends TableDefinition {

  object PortableJodaSupport extends GenericJodaSupport(dbConfiguration.driver)
  import dbConfiguration.driver.api._
  import PortableJodaSupport._

  class UserTable(tag: Tag) extends BaseTable[DBAccount](tag, "users") {

    val email = column[String]("email")

    def * =
      (
        id.?,
        email,
        createdAt,
        updatedAt
      ) <> (DBAccount.tupled, DBAccount.unapply _)
  }

  class OauthClientTable(tag: Tag)
      extends BaseTable[DBOauthClient](tag, "oauth_clients") {

    val ownerId = column[Long]("owner_id")
    val grantType = column[String]("grant_type")
    val clientId = column[String]("client_id")
    val clientSecret = column[String]("client_secret")
    val redirectUri = column[String]("redirect_uri")
    def * =
      (
        id.?,
        ownerId,
        grantType,
        clientId,
        clientSecret,
        redirectUri.?,
        createdAt,
        updatedAt
      ) <> (DBOauthClient.tupled, DBOauthClient.unapply _)
  }

  class OauthAuthorizationCodeTable(tag: Tag)
      extends BaseTable[DBOauthAuthorizationCode](
        tag,
        "oauth_authorization_codes"
      ) {

    val accountId = column[Long]("account_id")
    val oauthClientId = column[Long]("oauth_client_id")
    val code = column[String]("code")
    val redirectUri = column[String]("redirect_uri")
    def * =
      (
        id.?,
        accountId,
        oauthClientId,
        code,
        redirectUri.?,
        createdAt,
        updatedAt
      ) <> (DBOauthAuthorizationCode.tupled, DBOauthAuthorizationCode.unapply _)
  }

  class OAuthAccessTokenTable(tag: Tag)
      extends BaseTable[DBOAuthAccessToken](
        tag,
        "oauth_access_token"
      ) {
    val accountId = column[Long]("account_id")
    val oauthClientId = column[Long]("account_client_id")
    val accessToken = column[String]("access_token")
    val refreshToken = column[String]("refresh_token")

    def * =
      (
        id.?,
        accountId,
        oauthClientId,
        accessToken,
        refreshToken,
        createdAt,
        updatedAt
      ) <> (DBOAuthAccessToken.tupled, DBOAuthAccessToken.unapply _)
  }

  class LoginInfos(tag: Tag) extends BaseTable[DBLoginInfo](tag, "logininfo") {
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * =
      (id.?, providerID, providerKey, createdAt, updatedAt) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  class UserLoginInfos(tag: Tag)
      extends BaseTable[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[String]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def * =
      (id.?, userID, loginInfoId, createdAt, updatedAt) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  class PasswordInfos(tag: Tag)
      extends BaseTable[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * =
      (id.?, hasher, password, salt, loginInfoId, createdAt, updatedAt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  class OAuth1Infos(tag: Tag)
      extends BaseTable[DBOAuth1Info](tag, "oauth1info") {
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * =
      (id.?, token, secret, loginInfoId, createdAt, updatedAt) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  class OAuth2Infos(tag: Tag)
      extends BaseTable[DBOAuth2Info](tag, "oauth2info") {
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoid")
    def * =
      (
        id.?,
        accessToken,
        tokenType,
        expiresIn,
        refreshToken,
        loginInfoId,
        createdAt,
        updatedAt
      ) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

}
