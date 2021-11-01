package modules

import com.google.inject._
import com.google.inject.name.Named

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
import services.{
  UserService,
  UserServiceImpl,
  AuthTokenService,
  AuthTokenServiceImpl,
  MailService,
  DumpMailService,
  SMTPMailService,
  Oauth2DataHandler
}
import scala.concurrent.ExecutionContext.Implicits.global
import db.base.{DBConfiguration, CustomProfile}

import db.{TableDefinitions}
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import models.repositories.{
  OauthClientRepository,
  AccessTokenRepository,
  OauthAuthorizationCodeRepository,
  AuthInfoRepository,
  OauthClientRepositoryImpl,
  AccessTokenRepositoryImpl,
  OauthAuthorizationCodeRepositoryImpl,
  AuthInfoRepositoryImpl
}

class Oauth2Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[MailService]).to(classOf[SMTPMailService])
    bind(classOf[OauthClientRepository]).to(classOf[OauthClientRepositoryImpl])
    bind(classOf[OauthAuthorizationCodeRepository])
      .to(classOf[OauthAuthorizationCodeRepositoryImpl])
    bind(classOf[AccessTokenRepository]).to(classOf[AccessTokenRepositoryImpl])
    bind(classOf[OauthAuthorizationCodeRepository])
      .to(classOf[OauthAuthorizationCodeRepositoryImpl])
    bind(classOf[AuthTokenService]).to(classOf[AuthTokenServiceImpl])

    bind(classOf[AuthInfoRepository]).to(classOf[AuthInfoRepositoryImpl])

  }

}
