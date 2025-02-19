package io.letsstreamit.services.profile.infrastructure.adapters.repositories

import scala.collection.JavaConverters.*
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import com.mongodb.ErrorCategory
import io.letsstreamit.services.profile.core.interfaces.UserRepository
import io.letsstreamit.services.profile.core.models.User
import io.letsstreamit.services.profile.infrastructure.database.MongoDBConnection
import org.mongodb.scala.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes.*

/** MongoUserRepository
  * implementation of UserRepository using MongoDB
  * @param ec ExecutionContext for handling asynchronous operations
  */
class MongoUserRepository(implicit ec: ExecutionContext) extends UserRepository {

  private val collection = MongoDBConnection.database.getCollection("users")

  // Create a unique index on the email field
  collection.createIndex(ascending("email"), IndexOptions().unique(true)).toFuture().onComplete {
    case Success(indexName) => println(s"Unique index created on 'email': $indexName")
    case Failure(e) => println(s"Failed to create index: $e")
  }

  override def getUser(email: String): Future[Option[User]] = {
    collection.find(equal("email", email)).first().toFutureOption().map { docOpt =>
      docOpt.map(doc =>
        User(
          email = doc.getString("email"),
          username = doc.getString("username"),
          bio = doc.getString("bio") match {
            case bio => Some(bio)
            case null => None
          },
          videos = Some(doc.getList("videos", classOf[String]).asScala.toList)
        )
      )
    }
  }

  override def createUser(user: User): Future[Either[Exception, String]] = {
    val doc = Document(
      "email" -> user.email,
      "username" -> user.username,
      "bio" -> user.bio.getOrElse(""),
      "videos" -> user.videos.getOrElse(List.empty[String])
    )
    collection.insertOne(doc).toFuture().map(_ => Right(s"User ${user.email} created.")).recover {
      case e: MongoWriteException if e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY =>
        Left(new Exception(s"User with email ${user.email} already exists."))
      case e => Left(new Exception(s"Failed to create user: $e"))
    }
  }

  override def updateUser(user: User): Future[Either[Exception, String]] = {
    collection
      .updateOne(
        equal("email", user.email),
        Document(
          "$set" -> Document(
            "username" -> user.username,
            "bio" -> user.bio.getOrElse("")
          )
        )
      )
      .toFuture()
      .map { updateResult =>
        if (updateResult.getModifiedCount == 1) Right(s"User ${user.email} updated.")
        else Left(new Exception(s"Failed to update user ${user.email}."))
      }
  }

  override def addVideo(email: String, videoId: String): Future[Either[Exception, String]] = {
    collection.updateOne(equal("email", email), Document("$push" -> Document("videos" -> videoId))).toFuture().map {
      updateResult =>
        if (updateResult.getModifiedCount == 1) Right(s"Video $videoId added to user $email.")
        else Left(new Exception(s"Failed to add video $videoId to user $email."))
    }
  }
}
