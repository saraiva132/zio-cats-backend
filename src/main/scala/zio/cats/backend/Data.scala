package zio.cats.backend

import eu.timepit.refined.types.string.NonEmptyString

final case class UserId(value: String)         extends AnyVal
final case class Email private (value: String) extends AnyVal

final case class User(userId: UserId, email: Email, firstName: NonEmptyString, lastName: NonEmptyString)

final case class UserNotFound(userId: UserId) extends Throwable(s"User with $userId was not found!")
