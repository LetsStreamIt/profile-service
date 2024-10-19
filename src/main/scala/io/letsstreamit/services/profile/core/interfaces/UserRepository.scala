package io.letsstreamit.services.profile.core.interfaces

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.models.User

trait UserRepository {
  def getUser(email: String): Future[Option[User]]
  def createUser(user: User): Future[Either[Exception, String]]
  def updateUser(user: User): Future[Either[Exception, String]]
  def addVideo(email: String, videoId: String): Future[Either[Exception, String]]
}
