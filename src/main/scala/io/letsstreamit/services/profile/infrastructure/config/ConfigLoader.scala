package io.letsstreamit.services.profile.infrastructure.config

import java.time.Duration

import scala.util.Properties

import com.typesafe.config.ConfigFactory

/** ConfigLoader
  * loads the configuration for the profile service
  */
object ConfigLoader {
  private val config = ConfigFactory.load()

  /** requestTimeout
    * the timeout for the ask pattern
    */
  val requestTimeout: Duration = config.getDuration("profile-service.routes.ask-timeout")

  /** mongoUri
    * the uri for the mongo database
    */
  val mongoUri: String = Properties.envOrElse("MONGO_URI", config.getString("mongo.fallback-uri"))

  /** port
    * the port for the profile service
    */
  val port: Int = Properties.envOrElse("PORT", config.getInt("profile-service.port").toString).toInt

  /** requireAuth
    * whether the service requires authentication only inteded for testing
    */
  val requireAuth: Boolean = config.getBoolean("profile-service.require-auth")

  /** authUri
    * the uri for the auth service
    */
  val authUri: String = Properties.envOrElse("AUTH_SERVICE_URI", config.getString("auth-service.fallback-uri"))
}
