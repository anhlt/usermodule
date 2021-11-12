package models.repositories

import com.google.inject._
import db.{DBOauthAuthorizationCode, TableDefinitions}
import models.entities.User
import org.joda.time.DateTime

import java.security.SecureRandom
import java.util.UUID
import java.{util => ju}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

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

  implicit val exc: ExecutionContext = ex
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  def delete(codeValue: String): Future[Unit] = {

    val query = (for {
      authCode <- slickSAuthorizationCodes filter { code =>
        code.code === codeValue
      }
    } yield authCode).delete

    db.run(query).map(_ => {})
  }

  def randomString(len: Int): String =
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

    val action = for {
      _ <- slickSAuthorizationCodes += authorizationCode

    } yield ()

    db.run(action).map(_ => authorizationCode)

  }

}
