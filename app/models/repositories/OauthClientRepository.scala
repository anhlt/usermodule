package models.repositories

import com.google.inject._
import db.{DBOauthClient, TableDefinitions}

import scala.concurrent.{ExecutionContext, Future}

trait OauthClientRepository {

  def validate(
      clientId: String,
      clientSecret: String,
      grantType: String
  ): Future[Boolean]

  def findById(clientId: String): Future[Option[DBOauthClient]]
  def findClientCredentials(
      clientId: String,
      clientSecret: String
  ): Future[Option[DBOauthClient]]

  def findByIdAndRedirectUri(
      clientId: String,
      redirectUri: String
  ): Future[Option[DBOauthClient]]
}

class OauthClientRepositoryImpl @Inject()(
    val tableDefinations: TableDefinitions,
    ex: ExecutionContext
) extends OauthClientRepository {

  implicit val exc: ExecutionContext = ex
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  def validate(
      clientId: String,
      clientSecret: String,
      grantType: String
  ): Future[Boolean] = {
    db.run(
        slickSClients
          .filter(
            sclient =>
              sclient.clientId === clientId
                && sclient.clientSecret === clientSecret
                && sclient.grantType === grantType
          )
          .result
          .headOption
      )
      .map(_.isDefined)

  }

  def findById(clientId: String): Future[Option[DBOauthClient]] = {
    db.run(
      slickSClients
        .filter(sclient => sclient.clientId === clientId)
        .result
        .headOption
    )
  }

  def findByIdAndRedirectUri(
      clientId: String,
      redirectUri: String
  ): Future[Option[DBOauthClient]] = {
    db.run(
      slickSClients
        .filter(
          sclient =>
            sclient.clientId === clientId &&
              sclient.redirectUri === redirectUri
        )
        .result
        .headOption
    )
  }

  def findClientCredentials(
      clientId: String,
      clientSecret: String
  ): Future[Option[DBOauthClient]] = {
    db.run(
      slickSClients
        .filter(
          sclient =>
            sclient.clientId === clientId && sclient.clientSecret === clientSecret
        )
        .result
        .headOption
    )
  }

}
