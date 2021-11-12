package modules


import com.google.inject._
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.crypto._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import db.TableDefinitions
import db.base.{CustomProfile, DBConfiguration}
import models.repositories.{PasswordInfoRepository, UserRepository, UserRepositoryImp}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
import play.api.libs.ws.WSClient
import services.{UserService, UserServiceImpl}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImp])
    bind(classOf[IDGenerator]).toInstance(new SecureRandomIDGenerator())
    bind(classOf[FingerprintGenerator])
      .toInstance(new DefaultFingerprintGenerator())
    bind(classOf[EventBus]).toInstance(EventBus())
    bind(classOf[Clock]).toInstance(Clock())

    bind(classOf[DBConfiguration]).toInstance(new DBConfiguration {
      override val driver = CustomProfile
    })
    bind(classOf[TableDefinitions])

  }
  @Provides
  def provideSilhouette(
      env: Environment[DefaultEnv],
      securedAction: SecuredAction,
      unsecuredAction: UnsecuredAction,
      userAwareAction: UserAwareAction
  ): Silhouette[DefaultEnv] = {
    new SilhouetteProvider[DefaultEnv](
      env,
      securedAction,
      unsecuredAction,
      userAwareAction
    )
  }
  @Provides
  def providePasswordInfo(
      tableDefinition: TableDefinitions,
      exc: ExecutionContext
  ): DelegableAuthInfoDAO[PasswordInfo] = {
    new PasswordInfoRepository(tableDefinition, exc)
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideEnvironment(
      userService: UserService,
      authenticatorService: AuthenticatorService[JWTAuthenticator],
      eventBus: EventBus
  ): Environment[DefaultEnv] = {

    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying
      .as[JcaCrypterSettings]("silhouette.authenticator.crypter")

    new JcaCrypter(config)
  }
  @Provides
  def provideAuthenticatorService(
      @Named("authenticator-crypter") crypter: Crypter,
      idGenerator: IDGenerator,
      configuration: Configuration,
      clock: Clock
  ): AuthenticatorService[JWTAuthenticator] = {

    val config = configuration.underlying
      .as[JWTAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new JWTAuthenticatorService(config, None, encoder, idGenerator, clock)
  }

  @Provides
  def provideAuthInfoRepository(
      passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
      // oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
      // oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]
  ): AuthInfoRepository = {

    new DelegableAuthInfoRepository(
      passwordInfoDAO
      // oauth1InfoDAO,
      // oauth2InfoDAO
    )
  }

  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(
      new BCryptSha256PasswordHasher(),
      Seq(new BCryptPasswordHasher())
    )
  }

}
