package io.letsstreamit.services.profile.infrastructure.database

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Properties

import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase

object MongoDBConnection {
  private val mongoClient: MongoClient = MongoClient(Properties.envOrElse("MONGO_URI", "mongodb://localhost:27017"))
  val database: MongoDatabase = mongoClient.getDatabase("profile-db")

  def close()(implicit ec: ExecutionContext): Future[Unit] = Future {
    mongoClient.close()
  }
}
