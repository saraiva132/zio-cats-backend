package zio.cats.backend.data

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.refined._

final case class User(id: UserId, email: Email, first_name: NonEmptyString, last_name: NonEmptyString)

object User {
  implicit val codec: Codec[User] = deriveCodec

  val Test = User(UserId.Test, Email.Test, NonEmptyString.unsafeFrom("first"), NonEmptyString.unsafeFrom("last"))
}
