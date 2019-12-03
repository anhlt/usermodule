package db.migration.default

import slick.jdbc.MySQLProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import db.base.{DBConfiguration, TableDefinition, Entity}

import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.reflect.ClassTag

import db.{
  DBUser,
  DBOauthClient,
  DBOAuthAccessToken,
  DBOauthAuthorizationCode,
  DBLoginInfo,
  DBUserLoginInfo,
  DBPasswordInfo,
  DBOAuth1Info,
  DBOAuth2Info
}

import org.joda.time._
import scala.concurrent.Await
import scala.concurrent.duration._

class V1_2__new extends BaseJavaMigration {
  implicit val dialect: MySQLDialect = new MySQLDialect
  lazy val db = Database.forConfig("db.default")

  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
  }

  class UserTable(tag: Tag) extends BaseTable[DBUser](tag, "users") {

    val email = column[String]("email")

    def * =
      (
        id.?,
        email,
        createdAt,
        updatedAt
      ) <> (DBUser.tupled, DBUser.unapply _)
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
    def userID = column[Long]("userID")
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
      (hasher, password, salt, loginInfoId, createdAt, updatedAt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
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

  val userTable = TableQuery[UserTable]
  val oauthClientTable = TableQuery[OauthClientTable]
  val oauthAuthCoceTable = TableQuery[OauthAuthorizationCodeTable]
  val oAuthAccessTokenTable = TableQuery[OAuthAccessTokenTable]
  val loginInfoTable = TableQuery[LoginInfos]
  val userLoginInfoTable = TableQuery[UserLoginInfos]
  val passwordInfoTable = TableQuery[PasswordInfos]
  val oauth1InfoTable = TableQuery[OAuth1Infos]
  val oauth2InfoTable = TableQuery[OAuth2Infos]

  val m1 = TableMigration(userTable).create.addColumns(
    _.id,
    _.email,
    _.createdAt,
    _.updatedAt
  )
  val m2 = TableMigration(oauthClientTable).create.addColumns(
    _.id,
    _.ownerId,
    _.grantType,
    _.clientId,
    _.clientSecret,
    _.createdAt,
    _.updatedAt
  )
  val m3 = TableMigration(oauthAuthCoceTable).create.addColumns(
    _.id,
    _.accountId,
    _.code,
    _.oauthClientId,
    _.redirectUri,
    _.createdAt,
    _.updatedAt
  )
  val m4 = TableMigration(oAuthAccessTokenTable).create.addColumns(
    _.id,
    _.accountId,
    _.accessToken,
    _.refreshToken,
    _.createdAt,
    _.updatedAt
  )
  val m5 = TableMigration(loginInfoTable).create.addColumns(
    _.id,
    _.providerID,
    _.providerKey,
    _.createdAt,
    _.updatedAt
  )
  val m6 = TableMigration(passwordInfoTable).create.addColumns(
    _.id,
    _.hasher,
    _.password,
    _.salt,
    _.loginInfoId,
    _.createdAt,
    _.updatedAt
  )

  val m7 = TableMigration(oauth1InfoTable).create.addColumns(
    _.id,
    _.token,
    _.loginInfoId,
    _.secret,
    _.createdAt,
    _.updatedAt
  )

  val m8 = TableMigration(oauth2InfoTable).create.addColumns(
    _.id,
    _.accessToken,
    _.tokenType,
    _.expiresIn,
    _.refreshToken,
    _.loginInfoId,
    _.createdAt,
    _.updatedAt
  )

  def migrate(context: Context): Unit = {

    db.run(m1())
    db.run(m2())
    db.run(m3())
    db.run(m4())
    db.run(m5())
    db.run(m6())
    db.run(m7())
    db.run(m8())
  }

}
