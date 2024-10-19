package io.letsstreamit.services.profile.core.models

case class AuthData(id: String, email: String)
case class AuthResponse(data: AuthData)
