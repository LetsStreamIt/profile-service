package io.letsstreamit.services.profile.utils

import io.letsstreamit.services.profile.core.models.AuthData
import io.letsstreamit.services.profile.core.models.AuthResponse
import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.core.models.Video
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat4(User.apply)
  implicit val videoJsonFormat: RootJsonFormat[Video] = jsonFormat1(Video.apply)
}
object AuthJsonProtocol extends DefaultJsonProtocol {
  implicit val authDataJsonFormat: RootJsonFormat[AuthData] = jsonFormat2(AuthData.apply)
  implicit val authResponseJsonFormat: RootJsonFormat[AuthResponse] = jsonFormat1(AuthResponse.apply)
}
