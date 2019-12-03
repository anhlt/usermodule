package models.entities

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

case class User(id: Option[Long], loginInfo: LoginInfo, email: Option[String])
    extends Identity
