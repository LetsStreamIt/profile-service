package io.letsstreamit.services.profile.infrastructure.controllers

import scala.concurrent.Future

import io.letsstreamit.services.profile.application.usecases.UserUseCase
import io.letsstreamit.services.profile.core.models.User

class UserController(userUseCase: UserUseCase) {

  def getUser(email: String): Future[Option[User]] =
    userUseCase.fetchUser(email)

  def createUser(user: User): Future[Either[Exception, String]] =
    userUseCase.addUser(user)
}
