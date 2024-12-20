package io.letsstreamit.services.profile.infrastructure.routes

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.core.models.Video
import io.letsstreamit.services.profile.infrastructure.controllers.UserController
import io.letsstreamit.services.profile.utils.AuthenticationDirectives.getTokenData
import io.letsstreamit.services.profile.utils.AuthenticationDirectives.validateToken

/** UserRoutes
  * responsible for handling user related routes
  * @param userController UserController to interact with the database
  * @param system ActorSystem to handle the routes
  */
class UserRoutes(userController: UserController)(implicit system: ActorSystem[?]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
  import io.letsstreamit.services.profile.utils.JsonFormats.*

  system.log.info("UserRoutes started")
  val routes: Route =
    cors() {
      concat(
        pathPrefix("health") {
          pathEnd {
            get {
              complete(StatusCodes.OK)
            }
          }
        },
        validateToken(system) { token =>
          concat(
            pathPrefix("users") {
              concat(
                pathEnd {
                  // POST /users
                  post {
                    entity(as[User]) { user =>
                      onSuccess(createUser(user)) {
                        case Right(_) => complete(StatusCodes.Created)
                        case Left(_) => complete(StatusCodes.BadRequest)
                      }
                    }
                  }
                },
                path("update") {
                  // POST /users/update
                  post {
                    getTokenData(system) { email =>
                      entity(as[User]) { user =>
                        if (email != user.email) {
                          complete(StatusCodes.BadRequest)
                        }
                        onSuccess(updateUser(user)) {
                          case Right(_) => complete(StatusCodes.Created)
                          case Left(_) => complete(StatusCodes.BadRequest)
                        }
                      }
                    }
                  }
                },
                path(Segment) { email =>
                  // GET /users/{email}
                  get {
                    rejectEmptyResponse {
                      onSuccess(getUser(email)) {
                        case Some(user) => complete(user)
                        case None => complete(StatusCodes.NotFound)
                      }
                    }
                  }
                }
              )
            },
            path("videos") {
              getTokenData(system) { email =>
                // POST /videos
                post {
                  entity(as[Video]) { video =>
                    onSuccess(addVideo(email, video.videoId)) {
                      case Right(_) => complete(StatusCodes.Created)
                      case Left(_) => complete(StatusCodes.BadRequest)
                    }
                  }
                }
              }
            }
          )
        }
      )
    }

  def getUser(email: String): Future[Option[User]] =
    system.log.info(s"Getting user with email: $email")
    userController.getUser(email)

  def createUser(user: User): Future[Either[Exception, String]] =
    system.log.info(s"Creating user with email: ${user.email}")
    userController.createUser(user)

  def updateUser(user: User): Future[Either[Exception, String]] =
    system.log.info(s"Updating user with email: ${user.email}")
    userController.updateUser(user)

  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    system.log.info(s"Adding video with id: $videoId to user with email: $email")
    userController.addVideo(email, videoId)
}
