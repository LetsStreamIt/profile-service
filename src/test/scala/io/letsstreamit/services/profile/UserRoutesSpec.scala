package io.letsstreamit.services.profile

import scala.concurrent.Future

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.*
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.mongodb.MongoException
import io.letsstreamit.services.profile.application.usecases.UserUseCase
import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.core.models.Video
import io.letsstreamit.services.profile.core.services.UserService
import io.letsstreamit.services.profile.infrastructure.controllers.UserController
import io.letsstreamit.services.profile.infrastructure.routes.UserRoutes
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with MockFactory {

  lazy val testKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[?] = testKit.system
  // Create the mock
  val serviceMock = mock[UserService]

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
  import io.letsstreamit.services.profile.utils.JsonFormats.*
  val userUseCase = new UserUseCase(serviceMock)
  val userController = new UserController(userUseCase)
  val userRoutes = new UserRoutes(userController)

  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  "UserRoutes" should {
    "create a user" in {
      val user = User("email", "username", None, None)
      serviceMock.createUser.expects(user).returning(Future.successful(Right(s"User ${user.email} created.")))

      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "/users",
        entity = Marshal(user).to[RequestEntity].futureValue
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.Created)
      }
    }

    "not create a user if it already exists" in {
      val user = User("email", "username", None, None)
      serviceMock.createUser
        .expects(user)
        .returning(Future.successful(Left(new MongoException("User already exists."))))

      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "/users",
        entity = Marshal(user).to[RequestEntity].futureValue
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "update a user" in {
      val user = User("email", "username", None, None)
      serviceMock.updateUser.expects(user).returning(Future.successful(Right(s"User ${user.email} updated.")))

      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "/users/update",
        entity = Marshal(user).to[RequestEntity].futureValue
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.Created)
      }
    }

    "not update a user if the email is different" in {
      val user = User("email", "username", None, None)
      serviceMock.updateUser
        .expects(user)
        .returning(Future.successful(Left(new MongoException("Emails do not match."))))

      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "/users/update",
        entity = Marshal(user).to[RequestEntity].futureValue
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "get a user" in {
      val user = User("email", "username", None, None)
      serviceMock.findUserByEmail.expects(user.email).returning(Future.successful(Some(user)))

      val request = HttpRequest(
        method = HttpMethods.GET,
        uri = s"/users/${user.email}"
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[User] should ===(user)
      }
    }

    "not get a user if it does not exist" in {
      val user = User("email", "username", None, None)
      serviceMock.findUserByEmail.expects(user.email).returning(Future.successful(None))

      val request = HttpRequest(
        method = HttpMethods.GET,
        uri = s"/users/${user.email}"
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }

    "add a video to a user" in {
      val email = "test@email.com"
      val video = Video("videoId")
      serviceMock.addVideo
        .expects(email, video.videoId)
        .returning(Future.successful(Right(s"Video ${video.videoId} added to user $email.")))

      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "/videos",
        entity = Marshal(video).to[RequestEntity].futureValue
      )

      request ~> userRoutes.routes ~> check {
        status should ===(StatusCodes.Created)
      }
    }
  }
}
