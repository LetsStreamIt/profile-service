package io.letsstreamit.services.profile.application.usecases

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.core.services.UserService

/** UserUseCase
  * responsible for handling user related use cases
  * @param userService UserService to interact with the database
  */
class UserUseCase(userService: UserService) {

  /** fetchUser
    * fetches a user by email
    * @param email email of the user
    * @return Future[Option[User]] user if found, None otherwise
    */
  def fetchUser(email: String): Future[Option[User]] =
    userService.findUserByEmail(email)

  /** addUser
    * adds a user to the database
    * @param user user to be added
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def addUser(user: User): Future[Either[Exception, String]] =
    userService.createUser(user)

  /** updateUser
    * updates a user in the database
    * @param user user to be updated
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def updateUser(user: User): Future[Either[Exception, String]] =
    userService.updateUser(user)

  /** addVideo
    * adds a video to the user's list of videos
    * @param email email of the user
    * @param videoId id of the video
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    userService.addVideo(email, videoId)
}
