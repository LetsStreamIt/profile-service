package io.letsstreamit.services.profile.core.services

import scala.concurrent.Future

import io.letsstreamit.services.profile.core.interfaces.UserRepository
import io.letsstreamit.services.profile.core.models.User

/** UserService
  * responsible for handling user related operations
  * @param userRepository UserRepository to interact with the database
  */
class UserService(userRepository: UserRepository) {

  /** findUserByEmail
    * fetches a user by email
    * @param email email of the user
    * @return Future[Option[User]] user if found, None otherwise
    */
  def findUserByEmail(email: String): Future[Option[User]] =
    userRepository.getUser(email)

  /** createUser
    * adds a user to the database
    * @param user user to be added
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def createUser(user: User): Future[Either[Exception, String]] =
    userRepository.createUser(user)

  /** updateUser
    * updates a user in the database
    * @param user user to be updated
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def updateUser(user: User): Future[Either[Exception, String]] =
    userRepository.updateUser(user)

  /** addVideo
    * adds a video to the user's list of videos
    * @param email email of the user
    * @param videoId id of the video
    * @return Future[Either[Exception, String]] Right if successful, Left otherwise
    */
  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    userRepository.addVideo(email, videoId)
}
