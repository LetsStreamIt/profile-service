package io.letsstreamit.services.profile.core.services

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.interfaces.UserRepository
import io.letsstreamit.services.profile.core.models.User

class UserService(userRepository: UserRepository) {

  def findUserByEmail(email: String): Future[Option[User]] =
    userRepository.getUser(email)

  def createUser(user: User): Future[Either[Exception, String]] =
    // Add business validations or rules here if needed
    userRepository.createUser(user)
}
