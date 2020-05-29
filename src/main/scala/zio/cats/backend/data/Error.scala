package zio.cats.backend.data

import scala.util.control.NoStackTrace

sealed trait Error extends Throwable with NoStackTrace

object Error {

  final case class EmailNotValid(email: String) extends Throwable(s"$email is not a valid email.") with Error

  final case class EmailDoesNotMatch(userId: UserId, email: Email)
      extends Throwable(s"There is no user with email ${email.value} for ${userId.value}.")
      with Error

  final case class UserNotFound(userId: UserId) extends Throwable(s"User with id ${userId.value} was not found!") with Error

  final case class ErrorFetchingUser(userId: UserId, reason: String)
      extends Throwable(s"Error fetching user ${userId.value} with reason $reason!")
      with Error

  final case class DatabaseError(error: String) extends Throwable(s"Error accessing persistence layer: $error.") with Error

  final case class RequestTimedOut(message: String) extends Throwable(s"Timed out handling request. $message") with Error

}
