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

trait AccessTokenRepository {

  def create(
      user: User,
      client: DBOauthClient
  ): Future[AccessToken]

  def delete(
      user: User,
      client: DBOauthClient
  ): Future[Int]

  def refresh(user: User, client: DBOauthClient): Future[AccessToken]

  def findByAccessToken(accessToken: String): Future[Option[AccessToken]]

  def findByAuthotired(
      user: User,
      clientId: String
  ): Future[Option[AccessToken]]
}

class AccessTokenRepositoryImpl @Inject()(
    val tableDefinations: TableDefinitions,
    ex: ExecutionContext
) extends AccessTokenRepository {

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

  def create(
      user: User,
      client: DBOauthClient
  ): Future[AccessToken] = {

    def randomString(len: Int) =
      new Random(new SecureRandom()).alphanumeric.take(len).mkString

    val accessToken = DBOauthAccessToken(
      id = ju.UUID.randomUUID(),
      userId = user.id,
      oauthClientId = client.id,
      accessToken = randomString(40),
      refreshToken = randomString(40),
      createdAt = new DateTime()
    )

    val action = (for {
      _ <- slickSAccessTokenTable += accessToken

    } yield ())

    db.run(action).map(_ => accessToken.toEntity)
  }

  def delete(
      user: User,
      client: DBOauthClient
  ): Future[Int] = {
    // TODO: check sql

    val selectQuery = for {
      (token, client) <- slickSAccessTokenTable join slickSClients on (_.oauthClientId === _.id) filter ({
        case (
            token,
            client
            ) =>
          client.clientId === client.clientId
      })
    } yield token.id

    val deleteQuery =
      slickSAccessTokenTable.filter(token => token.id in selectQuery)

    db.run(deleteQuery.delete)
  }

  def refresh(user: User, client: DBOauthClient): Future[AccessToken] = {

    delete(user, client)
    create(user, client)
  }

  def findByAccessToken(accessToken: String): Future[Option[AccessToken]] = {
    db.run(
        slickSAccessTokenTable
          .filter(
            token => token.accessToken === accessToken
          )
          .result
          .headOption
      )
      .collect({
        case x => x.map(_.toEntity)
      })
  }

  def findByAuthotired(
      user: User,
      clientId: String
  ): Future[Option[AccessToken]] = {
    // TODO: check sql

    db.run((for {
        (token, client) <- slickSAccessTokenTable join slickSClients on (_.oauthClientId === _.id) filter ({
          case (
              token,
              client
              ) =>
            client.clientId === clientId
        })
      } yield token).result.headOption)
      .collect({
        case x => x.map(_.toEntity)
      })
  }

}
