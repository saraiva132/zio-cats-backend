package zio.cats.backend.data

import io.circe.{Decoder, Encoder}

final case class Email private (value: String) extends AnyVal

object Email {
  implicit val encoder: Encoder[Email] = Encoder.encodeString.contramap(_.value)
  implicit val decoder: Decoder[Email] = Decoder.decodeString.map(Email.apply)
}
