package modules

import com.google.inject._
import models.repositories._
import services.{AuthTokenService, AuthTokenServiceImpl, MailService, SMTPMailService}

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
