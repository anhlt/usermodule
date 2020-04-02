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

trait OauthAuthorizationCodeRepository {

  def delete(codeValue: String): Future[Unit]

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

}
