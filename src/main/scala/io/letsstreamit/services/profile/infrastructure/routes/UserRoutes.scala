package io.letsstreamit.services.profile.infrastructure.routes

import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.infrastructure.controllers.UserController

class UserRoutes(userController: UserController) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
  import io.letsstreamit.services.profile.utils.JsonFormats.*

  val routes: Route =
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
    }

  def getUser(email: String): Future[Option[User]] =
    userController.getUser(email)

  def createUser(user: User): Future[Either[Exception, String]] =
    userController.createUser(user)
}
