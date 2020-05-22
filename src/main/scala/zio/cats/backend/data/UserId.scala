package zio.cats.backend.data

import io.circe.{Decoder, Encoder}

final case class UserId(value: String) extends AnyVal

object UserId {
  implicit val encoder: Encoder[UserId] = Encoder.encodeString.contramap(_.value)
  implicit val decoder: Decoder[UserId] = Decoder.decodeString.map(UserId.apply)
}
