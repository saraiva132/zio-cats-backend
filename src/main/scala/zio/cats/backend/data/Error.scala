package zio.cats.backend.data

import scala.util.control.NoStackTrace

sealed trait Error extends Throwable with NoStackTrace
object Error {

  final case class EmailNotValid(email: String) extends Throwable(s"Email $email is not valid.") with Error

  final case class UserNotFound(userId: UserId) extends Throwable(s"User with $userId was not found!") with Error

  final case class ErrorFetchingUser(userId: UserId, reason: String)
      extends Throwable(s"Error fetching user $userId with reason $reason!")
      with Error

}
