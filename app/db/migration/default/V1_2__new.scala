package db.migration.default

import com.github.tototoshi.slick.PostgresJodaSupport._
import db._
import db.base.CustomProfile.api._
import db.base.{CustomProfile, Entity}
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import org.joda.time._
import slick.migration.api._

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.reflect.ClassTag

class V1_2__new extends BaseJavaMigration {
  implicit val dialect = GenericDialect(CustomProfile)
  lazy val db = Database.forConfig("db.default")

  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
  }

  class UserTable(tag: Tag)
      extends Table[(UUID, String, Boolean, DateTime, DateTime)](tag, "users") {

    def id = column[UUID]("id", O.PrimaryKey, O.SqlType("varchar(255)"))
    val email = column[String]("email")
    val activated = column[Boolean]("activated")
    val createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    val updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )

    def * = (id, email, activated, createdAt, updatedAt)
  }

  class LoginInfos(tag: Tag) extends BaseTable[DBLoginInfo](tag, "logininfo") {
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * =
      (id, providerID, providerKey, createdAt, updatedAt) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  class UserLoginInfos(tag: Tag)
      extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[UUID]("userID", O.SqlType("varchar(255)"))
    def loginInfoId = column[UUID]("loginInfoId", O.SqlType("varchar(255)"))
    def createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    def updatedAt = column[DateTime](
      "updated_date",
      O.SqlType("timestamp default now()")
    )
    def * =
      (userID, loginInfoId, createdAt, updatedAt) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  class PasswordInfos(tag: Tag)
      extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[UUID]("loginInfoId", O.SqlType("varchar(255)"))
    def createdAt =
      column[DateTime]("created_date", O.SqlType("timestamp default now()"))
    def updatedAt =
      column[DateTime]("updated_date", O.SqlType("timestamp default now()"))
    def * =
      (hasher, password, salt, loginInfoId, createdAt, updatedAt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  class OAuth1Infos(tag: Tag)
      extends BaseTable[DBOAuth1Info](tag, "oauth1info") {
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[UUID]("loginInfoId", O.SqlType("varchar(255)"))
    def * =
      (id, token, secret, loginInfoId, createdAt, updatedAt) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  class OAuth2Infos(tag: Tag)
      extends BaseTable[DBOAuth2Info](tag, "oauth2info") {
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[UUID]("logininfoid", O.SqlType("varchar(255)"))
    def * =
      (
        id,
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
  val loginInfoTable = TableQuery[LoginInfos]
  val userLoginInfoTable = TableQuery[UserLoginInfos]
  val passwordInfoTable = TableQuery[PasswordInfos]
  val oauth1InfoTable = TableQuery[OAuth1Infos]
  val oauth2InfoTable = TableQuery[OAuth2Infos]

  val m1 = TableMigration(userTable).create.addColumns(
    _.id,
    _.email,
    _.activated,
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

  val m9 = TableMigration(userLoginInfoTable).create.addColumns(
    _.userID,
    _.loginInfoId,
    _.createdAt,
    _.updatedAt
  )

  def migrate(context: Context): Unit = {
    implicit val ec = ExecutionContext.global

    val actions = (for {
      _ <- m1()
      _ <- m5()
      _ <- m6()
      _ <- m7()
      _ <- m8()
      _ <- m9()
    } yield ()).transactionally

    // db.run(m1())
    // db.run(m2())
    // db.run(m3())
    // db.run(m4())
    // db.run(m5())
    // db.run(m6())
    // db.run(m7())
    // db.run(m8())
    // db.run(m9())
    val rs = db.run(actions)

    Await.result(rs, 10 seconds)
  }

}
