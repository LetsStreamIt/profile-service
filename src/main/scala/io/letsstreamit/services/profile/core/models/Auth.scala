package io.letsstreamit.services.profile.core.models

/** AuthData
  * data to returned after a successful authentication
  * @param id user id
  * @param email user email
  */
case class AuthData(id: String, email: String)

/** AuthResponse
  * response to be returned after a successful authentication
  * @param data AuthData
  */
case class AuthResponse(data: AuthData)
