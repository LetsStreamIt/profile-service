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

/** AuthenticationDirectives
  * contains custom directives for authentication
  */
object AuthenticationDirectives {

  /** validateToken
    * validates the token provided in the Authorization header
    * @param system ActorSystem to handle the request
    * @return Directive1[String] token if valid
    */
  def validateToken(implicit system: ActorSystem[?]): Directive1[String] = {
    system.log.info("validating token")
    if (!ConfigLoader.requireAuth) {
      provide("no-auth")
    } else {
      headerValueByName("Authorization").flatMap { token =>
        // Make an HTTP request to the auth-service
        val responseFuture: Future[HttpResponse] = Http().singleRequest(
          HttpRequest(
            method = HttpMethods.POST,
            uri = s"http://${ConfigLoader.authServiceHostname}:${ConfigLoader.authServicePort}/api/auth/validate",
            headers = List(RawHeader("Authorization", token))
          )
        )
        system.log.info("Making request to auth-service")

        // Handle the response from the auth service
        onComplete(responseFuture).flatMap {
          case Success(response) if response.status == StatusCodes.OK =>
            system.log.info("Token is valid")
            provide(token) // Proceed if valid
          case Success(_) =>
            system.log.info("Token is invalid")
            complete(StatusCodes.Unauthorized -> "Invalid token") // Unauthorized if token is not valid
          case Failure(t) =>
            system.log.info(s"Error contacting auth-service: ${t.getMessage}")
            complete(
              StatusCodes.InternalServerError -> "Error contacting auth-service"
            ) // Error if something goes wrong
        }
      }
    }
  }

  /** getTokenData
    * gets the data from the token provided in the Authorization header
    * @param system ActorSystem to handle the request
    * @return Directive1[String] email if valid
    */
  def getTokenData(implicit system: ActorSystem[?]): Directive1[String] = {
    system.log.info("getting token data")
    if (!ConfigLoader.requireAuth) {
      provide("test@email.com")
    } else {
      headerValueByName("Authorization").flatMap { token =>
        // Make an HTTP request to the auth-service
        val responseFuture: Future[HttpResponse] = Http().singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = s"http://${ConfigLoader.authServiceHostname}:${ConfigLoader.authServicePort}/api/auth/data",
            headers = List(RawHeader("Authorization", token))
          )
        )

        system.log.info("Making request to auth-service")

        implicit val ec = system.executionContext
        // Handle the response from the auth service
        onComplete(responseFuture).flatMap {
          case Success(response) if response.status == StatusCodes.OK =>
            system.log.info("Token is valid")
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
            system.log.info("Token is invalid")
            complete(StatusCodes.Unauthorized -> "Invalid token") // Unauthorized if token is not valid
          case Failure(t) =>
            system.log.info(s"Error contacting auth-service: ${t.getMessage}")
            complete(
              StatusCodes.InternalServerError -> "Error contacting auth-service"
            ) // Error if something goes wrong
        }
      }
    }
  }
}
