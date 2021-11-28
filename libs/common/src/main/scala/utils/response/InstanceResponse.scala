package utils.response
import play.api.libs.json._
import models.entities.User


case class InstanceRespone[T](
    data: T
)

case class PagingResponse[T](
    elements: Seq[T],
    total: Int,
    hasNextPage: Boolean
)

case class OauthClientResponse(
    name: String,
    description: String
)

case class OauthAuthorizationCodeResponse(
    code: String,
    redirectUri: String
)
