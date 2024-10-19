package io.letsstreamit.services.profile.core.models

case class User(email: String, username: String, bio: Option[String], videos: Option[List[String]])
