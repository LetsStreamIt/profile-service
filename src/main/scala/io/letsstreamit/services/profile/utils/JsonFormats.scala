package io.letsstreamit.services.profile.utils

import io.letsstreamit.services.profile.core.models.User
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat3(User.apply)
}
