package services

import models.entities.User
import db.{TableDefinitions}
import scala.concurrent.ExecutionContext

import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future
import scala.util.Success
import scalaoauth2.provider.{
  AuthorizationHandler,
  DataHandler,
  AuthInfo,
  ClientCredential,
  AccessToken
}

import scala.concurrent.{Future, ExecutionContext}
import scalaoauth2.provider.AuthorizationRequest
import scalaoauth2.provider.AccessToken
import com.google.inject._
import scalaoauth2.provider.PasswordRequest
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import scalaoauth2.provider.ClientCredentialsRequest
import models.repositories.{
  OauthClientRepository,
  AccessTokenRepository,
  OauthAuthorizationCodeRepository,
  AuthInfoRepository
}
import scalaoauth2.provider.InvalidClient
import db.DBOauthAccessToken

class Oauth2DataHandler @Inject()(
    val tableDefinitions: TableDefinitions,
    userService: UserService,
    credentialsProvider: CredentialsProvider,
    oauthClientRepository: OauthClientRepository,
    accessTokenRepository: AccessTokenRepository,
    oauthAuthorizationCodeRepository: OauthAuthorizationCodeRepository,
    authInfoRepository: AuthInfoRepository,
    implicit val exc: ExecutionContext
) extends DataHandler[User] {

  import tableDefinitions._
  import tableDefinitions.dbConfiguration.driver.api._

  override def validateClient(
      maybeCredential: Option[ClientCredential],
      request: AuthorizationRequest
  ): Future[Boolean] = {

    maybeCredential match {
      case None => Future { false }
      case Some(credential) =>
        oauthClientRepository.validate(
          credential.clientId,
          credential.clientSecret.getOrElse(""),
          request.grantType
        )
    }

  }

  override def findUser(
      maybeCredential: Option[ClientCredential],
      request: AuthorizationRequest
  ): Future[Option[User]] = {
    request match {
      case rq: PasswordRequest =>
        val credentials = Credentials(rq.username, rq.password)
        credentialsProvider
          .authenticate(credentials)
          .flatMap { loginInfo =>
            userService.retrieve(loginInfo)
          }
      case rq: ClientCredentialsRequest =>
        val maybeUser = maybeCredential.map({ credential =>
          (for {
            maybeClient <- oauthClientRepository.findClientCredentials(
              credential.clientId,
              credential.clientSecret.get
            )
          } yield maybeClient)
            .collect({ case Some(client) => client })
            .flatMap({ dbClient =>
              userService.retrieve(dbClient.ownerId)
            })
        })

        maybeUser match {
          case Some(user) => user
          case None       => Future { None }
        }
      // The Client Credentials grant type is used by clients to obtain an access token outside of the context of a user.
      // This is typically used by clients to access resources about themselves rather than to access a user's resources.

    }
  }

  override def createAccessToken(
      authInfo: AuthInfo[User]
  ): Future[AccessToken] = {

    authInfo.clientId match {
      case Some(clientId) =>
        oauthClientRepository
          .findById(clientId)
          .collect({
            case Some(x) => x
            case _       => throw new InvalidClient()
          })
          .flatMap({ client =>
            accessTokenRepository.create(authInfo.user, client)
          })

      case _ => throw new InvalidClient()
    }
  }

  override def getStoredAccessToken(
      authInfo: AuthInfo[User]
  ): Future[Option[AccessToken]] = {
    accessTokenRepository
      .findByAuthotired(
        authInfo.user,
        authInfo.clientId.getOrElse("")
      )

  }

  override def refreshAccessToken(
      authInfo: AuthInfo[User],
      refreshToken: String
  ): Future[AccessToken] = {
    authInfo.clientId match {
      case Some(clientId) =>
        oauthClientRepository
          .findById(clientId)
          .collect({
            case Some(x) => x
            case _       => throw new InvalidClient()
          })
          .flatMap({ client =>
            accessTokenRepository.refresh(authInfo.user, client)
          })

      case _ => throw new InvalidClient()
    }
  }

  override def findAuthInfoByCode(
      code: String
  ): Future[Option[AuthInfo[User]]] = authInfoRepository.findByCode(code)

  override def deleteAuthCode(code: String): Future[Unit] =
    oauthAuthorizationCodeRepository.delete(code)

  override def findAuthInfoByRefreshToken(
      refreshToken: String
  ): Future[Option[AuthInfo[User]]] = {
    authInfoRepository.findByRefreshToken(refreshToken)
  }

  override def findAuthInfoByAccessToken(
      accessToken: AccessToken
  ): Future[Option[AuthInfo[User]]] = {
    authInfoRepository.findByAccessToken(accessToken.token)
  }

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    accessTokenRepository.findByAccessToken(token)
  }

}
