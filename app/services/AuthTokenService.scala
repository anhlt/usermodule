package services

import java.util.UUID

import db.AuthToken

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import scala.language.postfixOps
import models.repositories.AuthTokenRepository
import com.google.inject._
import org.joda.time.DateTime

/**
  * Handles actions to auth tokens.
  */
trait AuthTokenService {

  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userID The user ID for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  def create(
      userID: UUID,
      expiry: FiniteDuration = 5 minutes
  ): Future[AuthToken]

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  def validate(id: UUID): Future[Option[AuthToken]]

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  def clean: Future[Seq[AuthToken]]
}

class AuthTokenServiceImpl @Inject()(authTokenRepository: AuthTokenRepository)(
    implicit ex: ExecutionContext
) extends AuthTokenService {

  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userID The user ID for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  def create(userID: UUID, expiry: FiniteDuration = 12 hours) = {
    val token = AuthToken(
      UUID.randomUUID(),
      userID,
      DateTime.now().plus(expiry.toSeconds)
    )
    authTokenRepository.save(token).map(_ => token)
  }

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  def validate(id: UUID) = authTokenRepository.find(id)

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  def clean = authTokenRepository.findExpired().flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenRepository.remove(token.token).map(_ => token)
    })
  }
}
