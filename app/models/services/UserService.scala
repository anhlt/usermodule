package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import com.mohiva.play.silhouette.api.LoginInfo
import models.entities.User
import models.repositories.UserRepository

import scala.concurrent.{Future, ExecutionContext}
import com.google.inject._

/**
  * Handles actions to users.
  */
trait UserService extends IdentityService[User] {

  /**
    * Retrieves a user that matches the specified ID.
    *
    * @param id The ID to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  def retrieve(id: Long): Future[Option[User]]

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User]
}

class UserServiceImpl @Inject()(userRepository: UserRepository)(
    implicit ec: ExecutionContext
) extends UserService {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    userRepository.find(loginInfo)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = userRepository.save(user)

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile) = {
    userRepository.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userRepository.save(
          user.copy(
            email = profile.email
          )
        )
      case None => // Insert a new user
        userRepository.save(
          User(
            None,
            loginInfo = profile.loginInfo,
            email = profile.email,
            activated = false
          )
        )
    }
  }

  /**
    * Retrieves a user that matches the specified ID.
    *
    * @param id The ID to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  override def retrieve(id: Long): Future[Option[User]] =
    userRepository.find(id)

}
