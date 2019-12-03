package modules

import com.google.inject._
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.crypto._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{
  Environment,
  EventBus,
  Silhouette,
  SilhouetteProvider
}
import com.mohiva.play.silhouette.crypto.{
  JcaCrypter,
  JcaCrypterSettings,
  JcaSigner,
  JcaSignerSettings
}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth1._
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.{
  CookieSecretProvider,
  CookieSecretSettings
}
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2._
import com.mohiva.play.silhouette.impl.providers.state.{
  CsrfStateItemHandler,
  CsrfStateSettings
}
import com.mohiva.play.silhouette.impl.services._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.{
  BCryptPasswordHasher,
  BCryptSha256PasswordHasher
}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository

import com.typesafe.config.{Config}
import models.repositories.{
  UserRepository,
  PasswordInfoRepository,
  UserRepositoryImp
}

import play.api.{Configuration}

import play.api.libs.ws.WSClient
import play.api.mvc.Cookie
import utils.auth.DefaultEnv
import models.services.{UserService, UserServiceImpl}
import scala.concurrent.ExecutionContext.Implicits.global

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[Silhouette[DefaultEnv]])
      .to(classOf[SilhouetteProvider[DefaultEnv]])
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImp])
    bind(classOf[IDGenerator]).toInstance(new SecureRandomIDGenerator())
    bind(classOf[FingerprintGenerator]).toInstance(new DefaultFingerprintGenerator())
    bind(classOf[EventBus]).toInstance(EventBus())
    bind(classOf[Clock]).toInstance(Clock())



  }

}
