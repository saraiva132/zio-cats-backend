package zio.cats.backend.data

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

/**
 * ReqRes API already provides an email when creating an user.
 * Should this be removed? Should we ignore email from ReqRes API?
 **/
final case class PostUser(user_id: UserId, email: Email)

object PostUser {
  implicit val codec: Codec[PostUser] = deriveCodec

  val Test = PostUser(UserId.Test, Email.Test)
}
