package zio.cats.backend.data

import io.circe.{Decoder, Encoder}

final case class UserId(value: Int) extends AnyVal

object UserId {
  implicit val encoder: Encoder[UserId] = Encoder.encodeInt.contramap(_.value)
  implicit val decoder: Decoder[UserId] = Decoder.decodeInt.map(UserId.apply)

  val Test = UserId(1)
}
