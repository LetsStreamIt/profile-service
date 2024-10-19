package io.letsstreamit.services.profile.application.usecases

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.core.services.UserService

class UserUseCase(userService: UserService) {

  def fetchUser(email: String): Future[Option[User]] =
    userService.findUserByEmail(email)

  def addUser(user: User): Future[Either[Exception, String]] =
    userService.createUser(user)

  def updateUser(user: User): Future[Either[Exception, String]] =
    userService.updateUser(user)

  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    userService.addVideo(email, videoId)
}
