package zio.cats.backend.data

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.refined._

final case class User(userId: UserId, email: Email, firstName: NonEmptyString, lastName: NonEmptyString)

object User {
  implicit val codec: Codec[User] = deriveCodec

  val Test = User(UserId.Test, Email.Test, NonEmptyString.unsafeFrom("first"), NonEmptyString.unsafeFrom("last"))
}
