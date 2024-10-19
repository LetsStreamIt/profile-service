package io.letsstreamit.services.profile.infrastructure.config

import java.time.Duration

import scala.util.Properties

import com.typesafe.config.ConfigFactory

object ConfigLoader {
  private val config = ConfigFactory.load()

  val requestTimeout: Duration = config.getDuration("profile-service.routes.ask-timeout")

  val mongoUri: String = Properties.envOrElse("MONGO_URI", config.getString("mongo.fallback-uri"))

  val port: Int = config.getInt("profile-service.port")

  val requireAuth: Boolean = config.getBoolean("profile-service.require-auth")

  val authUri: String = Properties.envOrElse("AUTH_SERVICE_URI", config.getString("auth-service.fallback-uri"))
}
