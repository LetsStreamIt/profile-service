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

class UserRoutes(userController: UserController)(implicit system: ActorSystem[?]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
  import io.letsstreamit.services.profile.utils.JsonFormats.*

  val routes: Route =
    validateToken(system) { token =>
      concat(
        pathPrefix("users") {
          concat(
            pathEnd {
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

  def getUser(email: String): Future[Option[User]] =
    userController.getUser(email)

  def createUser(user: User): Future[Either[Exception, String]] =
    userController.createUser(user)

  def updateUser(user: User): Future[Either[Exception, String]] =
    userController.updateUser(user)

  def addVideo(email: String, videoId: String): Future[Either[Exception, String]] =
    userController.addVideo(email, videoId)
}
