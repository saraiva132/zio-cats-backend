package zio.cats.backend.data

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class PostUser(userId: UserId, email: Email)

object PostUser {
  implicit val codec: Codec[PostUser] = deriveCodec
}
