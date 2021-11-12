package models.repositories

import com.google.inject._
import com.mohiva.play.silhouette.api.LoginInfo
import db.{DBOauthAccessToken, TableDefinitions}
import models.entities.User
import scalaoauth2.provider.{AccessToken, AuthInfo}

import scala.concurrent.{ExecutionContext, Future}

trait AuthInfoRepository {

  def findByAccessToken(
      accessToken: String
  ): Future[Option[AuthInfo[User]]]

  def findByRefreshToken(
      refreshToken: String
  ): Future[Option[AuthInfo[User]]]

  def findByCode(
      code: String
  ): Future[Option[AuthInfo[User]]]
}

class AuthInfoRepositoryImpl @Inject()(
    val tableDefinations: TableDefinitions,
    ex: ExecutionContext
) extends AuthInfoRepository {

  implicit val exc = ex
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  import scala.language.implicitConversions
  implicit class Helper(
      dbAccessToken: DBOauthAccessToken
  ) {

    def toEntity =
      AccessToken(
        dbAccessToken.accessToken,
        Some(dbAccessToken.refreshToken),
        None,
        Some(3600),
        dbAccessToken.createdAt.toDate
      )
  }

  def findByAccessToken(
      accessToken: String
  ): Future[Option[AuthInfo[User]]] = {
    db.run((for {
        token <- slickSAccessTokenTable filter (_.accessToken === accessToken)
        dbUser <- slickUsers filter (_.id === token.userId)
        dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
        dbLoginInfo <- slickLoginInfos.filter(
          _.id === dbUserLoginInfo.loginInfoId
        )
        client <- slickSClients filter (_.id === token.oauthClientId)
      } yield (token, dbUser, dbLoginInfo, client)).result.headOption)
      .collect({
        case rs @ Some((token, dbUser, dbLoginInfo, client)) =>
          val user = User(
            dbUser.id,
            LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
            Some(dbUser.email),
            dbUser.username,
            dbUser.nickname,
            dbUser.activated
          )

          rs.map(
            _ =>
              AuthInfo(
                user = user,
                clientId = Some(client.clientId),
                scope = None,
                redirectUri = None
              )
          )

      })
  }

  def findByRefreshToken(
      refreshToken: String
  ): Future[Option[AuthInfo[User]]] = {
    db.run((for {
        token <- slickSAccessTokenTable filter (_.refreshToken === refreshToken)
        dbUser <- slickUsers filter (_.id === token.userId)
        dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
        dbLoginInfo <- slickLoginInfos.filter(
          _.id === dbUserLoginInfo.loginInfoId
        )
        client <- slickSClients filter (_.id === token.oauthClientId)
      } yield (token, dbUser, dbLoginInfo, client)).result.headOption)
      .collect({
        case rs @ Some((token, dbUser, dbLoginInfo, client)) =>
          val user = User(
            dbUser.id,
            LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
            Some(dbUser.email),
            dbUser.username,
            dbUser.nickname,
            dbUser.activated
          )

          rs.map(
            _ =>
              AuthInfo(
                user = user,
                clientId = Some(client.clientId),
                scope = None,
                redirectUri = None
              )
          )

      })
  }
  def findByCode(
      code: String
  ): Future[Option[AuthInfo[User]]] = {
    db.run((for {
        authCode <- slickSAuthorizationCodes filter (_.code === code)
        dbUser <- slickUsers filter (_.id === authCode.userId)
        dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
        dbLoginInfo <- slickLoginInfos.filter(
          _.id === dbUserLoginInfo.loginInfoId
        )
        client <- slickSClients filter (_.id === authCode.oauthClientId)
      } yield (authCode, dbUser, dbLoginInfo, client)).result.headOption)
      .collect({
        case rs @ Some((token, dbUser, dbLoginInfo, client)) =>
          val user = User(
            dbUser.id,
            LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
            Some(dbUser.email),
            dbUser.username,
            dbUser.nickname,
            dbUser.activated
          )

          rs.map(
            _ =>
              AuthInfo(
                user = user,
                clientId = Some(client.clientId),
                scope = None,
                redirectUri = None
              )
          )
        case rs @ None => None
      })
  }

}
