package models.repositories

import scala.concurrent.{Future, ExecutionContext}
import scala.reflect.ClassTag
import com.google.inject._
import java.util.UUID

import db.TableDefinitions
import db.{
  DBUser,
  DBLoginInfo,
  DBUserLoginInfo,
  DBPasswordInfo,
  DBOauthClient,
  DBOauthAccessToken
}
import models.entities.User
import java.security.SecureRandom
import scala.util.Random
import org.joda.time.DateTime
import java.{util => ju}
import scalaoauth2.provider.AccessToken
import scalaoauth2.provider.AuthInfo
import com.mohiva.play.silhouette.api.LoginInfo
import db.DBOauthAuthorizationCode

trait OauthAuthorizationCodeRepository {

  def delete(codeValue: String): Future[Unit]
  def create(
      user: User,
      clientId: UUID,
      redirectUri: Option[String]
  ): Future[DBOauthAuthorizationCode]

}

class OauthAuthorizationCodeRepositoryImpl @Inject()(
    val tableDefinations: TableDefinitions,
    ex: ExecutionContext
) extends OauthAuthorizationCodeRepository {

  implicit val exc = ex
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  def delete(codeValue: String): Future[Unit] = {

    val query = (for {
      authCode <- slickSAuthorizationCodes filter ({ code =>
        code.code === codeValue
      })
    } yield authCode).delete

    db.run(query).map(_ => {})
  }

  def randomString(len: Int) =
    new Random(new SecureRandom()).alphanumeric.take(len).mkString

  override def create(
      user: User,
      clientId: UUID,
      redirectUri: Option[String]
  ): Future[DBOauthAuthorizationCode] = {
    val authorizationCode = DBOauthAuthorizationCode(
      id = ju.UUID.randomUUID(),
      userId = user.id,
      oauthClientId = clientId,
      code = randomString(40),
      redirectUri = redirectUri,
      createdAt = new DateTime()
    )

    val action = (for {
      _ <- slickSAuthorizationCodes += authorizationCode

    } yield ())

    db.run(action).map(_ => authorizationCode)

  }

}
