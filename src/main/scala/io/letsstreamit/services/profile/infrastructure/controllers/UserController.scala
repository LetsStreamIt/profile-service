package io.letsstreamit.services.profile.infrastructure.controllers

import scala.concurrent.Future

import io.letsstreamit.services.profile.application.usecases.UserUseCase
import io.letsstreamit.services.profile.core.models.User

/** UserController
  * responsible for handling user related requests
  * @param userUseCase UserUseCase to interact with the database
  */
class UserController(userUseCase: UserUseCase) {

  /** getUser
    * fetches a user by email
    * @param email email of the user
    * @return Future[Option[User]] user if found, None otherwise
    */
  def getUser(email: String): Future[Option[User]] =
    userUseCase.fetchUser(email)

  /** createUser
    * adds a user to the database
    * @param user user to be added
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def createUser(user: User): Future[Either[Exception, String]] =
    userUseCase.addUser(user)

  /** updateUser
    * updates a user in the database
    * @param user user to be updated
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def updateUser(user: User): Future[Either[Exception, String]] =
    userUseCase.updateUser(user)

  /** addVideo
    * adds a video to the user's list of videos
    * @param email email of the user
    * @param videoId id of the video
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    userUseCase.addVideo(email, videoId)
}
