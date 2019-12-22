package models.repositories
import db.TableDefinitions
import db.{DBUser, DBLoginInfo, DBUserLoginInfo}

import com.google.inject._
import com.google.inject.name.{Names, Named}
import scala.concurrent.Future
import models.entities.User
import com.mohiva.play.silhouette.api.LoginInfo
import scala.concurrent.ExecutionContext
import play.api.Logging
import java.{util => ju}
import tyrex.services.UUID
import db.DBUserRoles
import utils.auth.Role

class UserRepositoryImp @Inject()(
    val tableDefinations: TableDefinitions,
    implicit val ex: ExecutionContext
) extends UserRepository
    with Logging {
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    // val userQuery = for {
    //   dbLoginInfo <- loginInfoQuery(loginInfo)
    //   dbUserLoginInfo <- slickUserLoginInfos.filter(
    //     _.loginInfoId === dbLoginInfo.id
    //   )
    //   dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    // } yield dbUser

    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(
        _.loginInfoId === dbLoginInfo.id
      )
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(
          user.id,
          loginInfo,
          Some(user.email),
          user.activated
        )
      }
    }
  }

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: ju.UUID): Future[Option[User]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(
        _.id === dbUserLoginInfo.loginInfoId
      )
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            user.id,
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            Some(user.email),
            user.activated
          )
      }
    }
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User] = {
    logger.info(s" Save User ${user}")
    val dbUser = DBUser(user.id, user.email.get, user.activated)
    val dbLoginInfo =
      DBLoginInfo(
        ju.UUID.randomUUID(),
        user.loginInfo.providerID,
        user.loginInfo.providerKey
      )
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos
        .filter(
          info =>
            info.providerID === user.loginInfo.providerID &&
              info.providerKey === user.loginInfo.providerKey
        )
        .result
        .headOption

      val q = slickLoginInfos.filter(
        info =>
          info.providerID === user.loginInfo.providerID &&
            info.providerKey === user.loginInfo.providerKey
      )

      val insertLoginInfo = slickLoginInfos += dbLoginInfo

      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption
          .map(x => {
            logger.info(s"$x")
            DBIO.successful(x)
          })
          .getOrElse({
            logger.info("===>")
            insertLoginInfo
          })
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      _ <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(
        dbUser.id,
        dbLoginInfo.id
      )
      _ <- slickUserRoles += DBUserRoles(
        dbUser.id,
        Role.OrdinaryUser.toString
      )
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
