package io.letsstreamit.services.profile.infrastructure.database

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import io.letsstreamit.services.profile.infrastructure.config.ConfigLoader
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase

/** MongoDBConnection
  * responsible for handling the connection to the mongo database
  */
object MongoDBConnection {
  private val mongoClient: MongoClient = MongoClient(
    s"mongodb://${ConfigLoader.mongoHostname}:${ConfigLoader.mongoPort}/profile"
  )

  /** database
    * the database to be used
    */
  val database: MongoDatabase = mongoClient.getDatabase("profile-db")

  def close()(implicit ec: ExecutionContext): Future[Unit] = Future {
    mongoClient.close()
  }
}
