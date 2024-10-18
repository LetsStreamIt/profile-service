package io.letsstreamit.services.profile.core.interfaces

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.models.User

trait UserRepository {
  def getUser(email: String): Future[Option[User]]
  def createUser(user: User): Future[Either[Exception, String]]
}
