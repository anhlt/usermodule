package db.migration.default

import db.base.CustomProfile
import db.base.CustomProfile.api._
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

class V2_1__add_client_name extends BaseJavaMigration {

  implicit val dialect = GenericDialect(CustomProfile)
  lazy val db = Database.forConfig("db.default")

  case class ServerOauthClient(
      id: UUID,
      ownerId: UUID,
      grantType: String,
      clientId: String,
      clientSecret: String,
      redirectUri: Option[String],
      clientName: String,
      clientDescription: String,
      createdAt: DateTime
  )

  class ServerOauthClientTable(tag: Tag)
      extends Table[ServerOauthClient](tag, "oauth_client") {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val ownerId = column[UUID]("owner_id", O.SqlType("varchar(255)"))
    val grantType = column[String]("grant_type")
    val clientId = column[String]("client_id")
    val clientSecret = column[String]("client_secret")
    val clientName = column[String]("client_name")
    val clientDescription =
      column[String]("client_description", O.SqlType("varchar(255)"))
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
        clientName,
        clientDescription,
        createdAt
      ) <> (ServerOauthClient.tupled, ServerOauthClient.unapply)

  }

  val clientTable = TableQuery[ServerOauthClientTable]

  val m3 = TableMigration(clientTable).addColumns(
    _.clientName,
    _.clientDescription
  )

  def migrate(context: Context): Unit = {
    implicit val ec = ExecutionContext.global

    val actions = (for {
      _ <- m3()
    } yield ()).transactionally

    Await.result(db.run(actions), 10 seconds)
  }
}
