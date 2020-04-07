package models.services

import scalaoauth2.provider.TokenEndpoint
import scalaoauth2.provider.AuthorizationCode
import scalaoauth2.provider.RefreshToken
import scalaoauth2.provider.ClientCredentials
import scalaoauth2.provider.Password
import scalaoauth2.provider.OAuthGrantType

class MyTokenEndpoint extends TokenEndpoint {
  override val handlers = Map(
    OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode(),
    OAuthGrantType.REFRESH_TOKEN -> new RefreshToken(),
    OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials(),
    OAuthGrantType.PASSWORD -> new Password()
  )
}

object MyTokenEndpoint extends MyTokenEndpoint
