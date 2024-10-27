package io.letsstreamit.services.profile.application

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.util.Failure
import scala.util.Success

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import io.letsstreamit.services.profile.application.usecases.UserUseCase
import io.letsstreamit.services.profile.core.services.UserService
import io.letsstreamit.services.profile.infrastructure.adapters.repositories.MongoUserRepository
import io.letsstreamit.services.profile.infrastructure.config.ConfigLoader
import io.letsstreamit.services.profile.infrastructure.controllers.UserController
import io.letsstreamit.services.profile.infrastructure.routes.UserRoutes

/** ProfileServiceApp
  * contains the main method to start the Profile Service
  */
object ProfileServiceApp {

  def main(args: Array[String]): Unit = {
    val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>

      val timeout: Timeout =
        Timeout.create(ConfigLoader.requestTimeout)

      implicit val ec: ExecutionContext = ExecutionContext.global

      val userRepository = new MongoUserRepository

      // Initialize Core Services
      val userService = new UserService(userRepository)

      // Initialize Application Use Cases
      val userUseCase = new UserUseCase(userService)

      // Initialize Controllers
      val userController = new UserController(userUseCase)

      // Initialize HTTP Routes
      val userRoutes = new UserRoutes(userController)(context.system).routes

      // Combine all routes
      val routes: Route = userRoutes // concat(userRoutes, profilePictureRoutes)

      // Start HTTP server
      startHttpServer(routes)(context.system)

      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "ProfileServiceSystem")

    // Block the main thread until the ActorSystem terminates
    Await.result(system.whenTerminated, Duration.Inf)
  }

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[?]): Unit = {
    import system.executionContext

    val host = ConfigLoader.host
    val port = ConfigLoader.port
    val futureBinding = Http().newServerAt(host, port).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

}
