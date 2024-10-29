package io.letsstreamit.services.profile.infrastructure.config

import java.time.Duration

import scala.util.Properties

import com.typesafe.config.ConfigFactory

/** ConfigLoader
  * loads the configuration for the profile service
  */
object ConfigLoader {
  private val config = ConfigFactory.load()

  /** address
    * the address for the profile service
    */
  val host: String = Properties.envOrElse("PROFILE_SERVICE_HOSTNAME", config.getString("profile-service.host"))

  /** requestTimeout
    * the timeout for the ask pattern
    */
  val requestTimeout: Duration = config.getDuration("profile-service.routes.ask-timeout")

  /** mongoHostname
    * the hostname for the mongo database
    */
  val mongoHostname: String = Properties.envOrElse("MONGO_HOSTNAME", config.getString("mongo.hostname"))

  /** mongoPort
    * the port for the mongo database
    */
  val mongoPort: Int = Properties.envOrElse("MONGO_PORT", config.getInt("mongo.port").toString).toInt

  /** port
    * the port for the profile service
    */
  val port: Int = Properties.envOrElse("PROFILE_SERVICE_PORT", config.getInt("profile-service.port").toString).toInt

  /** requireAuth
    * whether the service requires authentication only inteded for testing
    */
  val requireAuth: Boolean = config.getBoolean("profile-service.require-auth")

  /** authServiceHostname
    * the hostname for the auth service
    */
  val authServiceHostname: String =
    Properties.envOrElse("AUTH_SERVICE_HOSTNAME", config.getString("auth-service.hostname"))

  /** authServicePort
    * the port for the auth service
    */
  val authServicePort: Int =
    Properties.envOrElse("AUTH_SERVICE_PORT", config.getInt("auth-service.port").toString).toInt

}
