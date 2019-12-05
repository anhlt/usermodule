package models.repositories
import db.AuthToken
import com.google.inject._
import scala.concurrent.ExecutionContext
import db.TableDefinitions
import org.joda.time.DateTime
import java.util.UUID
import com.github.tototoshi.slick.GenericJodaSupport
import scala.concurrent.Future

class AuthTokenRepository @Inject()(
    val tableDefinations: TableDefinitions,
    implicit val ex: ExecutionContext
) {
  object PortableJodaSupport
      extends GenericJodaSupport(tableDefinations.dbConfiguration.driver)
  import tableDefinations._
  import tableDefinations.dbConfiguration.driver.api._
  import PortableJodaSupport._

  def find(token: UUID) = {

    db.run(slickAuthTokens.filter(_.token === token).result.headOption)
  }

  /**
    * Finds expired tokens.
    *
    */
  def findExpired(): Future[Seq[AuthToken]] = {
    // case class AuthToken(token: UUID, userID: Long, expiry: DateTime)

    db.run(slickAuthTokens.filter(_.expiry < DateTime.now()).result)
  }

  /**
    * Saves a token.
    *
    * @param token The token to save.
    * @return The saved token.
    */
  def save(token: AuthToken) = {
    // case class DBAuthToken(token: String, userID: Long, expiry: DateTime)

    db.run(slickAuthTokens.insertOrUpdate(token))
  }

  /**
    * Removes the token for the given ID.
    *
    * @param id The ID for which the token should be removed.
    * @return A future to wait for the process to be completed.
    */
  def remove(id: UUID) = {
    db.run(slickAuthTokens.filter(_.token === id).delete)
  }
}
