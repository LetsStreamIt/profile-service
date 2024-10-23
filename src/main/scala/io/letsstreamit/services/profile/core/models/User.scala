package io.letsstreamit.services.profile.core.models

/** User
  * data model for a user
  * @param email email of the user
  * @param username username of the user
  * @param bio bio of the user
  * @param videos list of video ids the user has watched
  */
case class User(email: String, username: String, bio: Option[String], videos: Option[List[String]])
