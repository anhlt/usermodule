package db.migration.default

import db.base.CustomMySqlProfile
import db.base.CustomMySqlProfile.api._
import slick.migration.api._
import slick.migration.api.flyway._
import slick.migration.api.flyway.UnmanagedDatabase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import db.base.{DBConfiguration, TableDefinition, Entity}

import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.reflect.ClassTag
import org.joda.time._
import java.util.UUID
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class V2_oauth_service extends BaseJavaMigration {

  implicit val dialect = GenericDialect(CustomMySqlProfile)
  lazy val db = Database.forConfig("db.default")

  case class ServerOauthClient(
      id: UUID,
      ownerId: UUID,
      grantType: String,
      clientId: String,
      clientSecret: String,
      redirectUri: Option[String],
      createdAt: DateTime
  )

  case class ServerOauthAuthorizationCode(
      id: UUID,
      userId: UUID,
      oauthClientId: UUID,
      code: String,
      redirectUri: Option[String],
      createdAt: DateTime
  )

  case class ServerOauthAccessToken(
      id: UUID,
      userId: UUID,
      oauthClientId: UUID,
      accessToken: String,
      refreshToken: String,
      createdAt: DateTime
  )

  class ServerOauthClientTable(tag: Tag)
      extends Table[ServerOauthClient](tag, "oathserver_oauth_client") {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val ownerId = column[UUID]("owner_id", O.SqlType("varchar(255)"))
    val grantType = column[String]("grant_type")
    val clientId = column[String]("client_id")
    val clientSecret = column[String]("client_secret")
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
        createdAt
      ) <> (ServerOauthClient.tupled, ServerOauthClient.unapply)

  }

  class ServerOauthAuthorizationCodeTable(tag: Tag)
      extends Table[ServerOauthAuthorizationCode](
        tag,
        "oathserver_oauth_authorization_code"
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
      ) <> (ServerOauthAuthorizationCode.tupled, ServerOauthAuthorizationCode.unapply)

  }

  class ServerOauthAccessTokenTable(tag: Tag)
      extends Table[ServerOauthAccessToken](
        tag,
        "oathserver_oauth_authorization_code"
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
      ) <> (ServerOauthAccessToken.tupled, ServerOauthAccessToken.unapply)

  }

  val accessTokenTable = TableQuery[ServerOauthAccessTokenTable]
  val oauthAuthorizationCodeTable =
    TableQuery[ServerOauthAuthorizationCodeTable]
  val clientTable = TableQuery[ServerOauthClientTable]

  val m1 = TableMigration(accessTokenTable).create.addColumns(
    _.id,
    _.userId,
    _.oauthClientId,
    _.accessToken,
    _.refreshToken,
    _.createdAt
  )

  val m2 = TableMigration(oauthAuthorizationCodeTable).create.addColumns(
    _.id,
    _.userId,
    _.oauthClientId,
    _.code,
    _.redirectUri,
    _.createdAt
  )

  val m3 = TableMigration(clientTable).create.addColumns(
    _.id,
    _.grantType,
    _.clientId,
    _.clientSecret,
    _.clientSecret,
    _.redirectUri,
    _.createdAt
  )

  def migrate(context: Context): Unit = {
    implicit val ec = ExecutionContext.global

    val actions = (for {
      _ <- m1()
      _ <- m2()
      _ <- m3()
    } yield ()).transactionally

    Await.result(db.run(actions), 10 seconds)
  }
}
