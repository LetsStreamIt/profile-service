package io.letsstreamit.services.profile.utils

import scala.concurrent.Await
import scala.concurrent.Future
import scala.jdk.DurationConverters.*
import scala.util.Failure
import scala.util.Success

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.unmarshalling.Unmarshal
import io.letsstreamit.services.profile.core.models.AuthResponse
import io.letsstreamit.services.profile.infrastructure.config.ConfigLoader
import io.letsstreamit.services.profile.utils.AuthJsonProtocol.*

object AuthenticationDirectives {

  // Custom Directive to validate token
  def validateToken(implicit system: ActorSystem[?]): Directive1[String] = {
    headerValueByName("Authorization").flatMap { token =>
      // Make an HTTP request to the auth-service
      val responseFuture: Future[HttpResponse] = Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = s"${ConfigLoader.authUri}/api/auth/validate",
          headers = List(RawHeader("Authorization", token))
        )
      )

      // Handle the response from the auth service
      onComplete(responseFuture).flatMap {
        case Success(response) if response.status == StatusCodes.OK =>
          provide(token) // Proceed if valid
        case Success(_) =>
          complete(StatusCodes.Unauthorized -> "Invalid token") // Unauthorized if token is not valid
        case Failure(_) =>
          complete(
            StatusCodes.InternalServerError -> "Error contacting auth-service"
          ) // Error if something goes wrong
      }
    }
  }

  def getTokenData(implicit system: ActorSystem[?]): Directive1[String] = {
    headerValueByName("Authorization").flatMap { token =>
      // Make an HTTP request to the auth-service
      val responseFuture: Future[HttpResponse] = Http().singleRequest(
        HttpRequest(
          method = HttpMethods.GET,
          uri = s"${ConfigLoader.authUri}/api/auth/data",
          headers = List(RawHeader("Authorization", token))
        )
      )

      implicit val ec = system.executionContext
      // Handle the response from the auth service
      onComplete(responseFuture).flatMap {
        case Success(response) if response.status == StatusCodes.OK =>
          provide(
            Await
              .result(
                Unmarshal(response.entity)
                  .to[AuthResponse]
                  .recover(_ => complete(StatusCodes.InternalServerError -> "Error parsing response")),
                ConfigLoader.requestTimeout.toScala
              ) match
              case AuthResponse(data) => data.email
              case _: StandardRoute => ""
          )
        case Success(_) =>
          complete(StatusCodes.Unauthorized -> "Invalid token") // Unauthorized if token is not valid
        case Failure(_) =>
          complete(
            StatusCodes.InternalServerError -> "Error contacting auth-service"
          ) // Error if something goes wrong
      }
    }
  }
}
