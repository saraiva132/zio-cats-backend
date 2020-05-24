package zio.cats.backend.http

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.ZIO
import zio.cats.backend.data.Error._
import zio.logging.{Logging, log}

sealed trait ClientError extends Product with Serializable

object ClientError {

  final case class InternalServerError(message: String) extends ClientError
  object InternalServerError {
    implicit val codec: Codec[InternalServerError] = deriveCodec
  }
  final case class BadRequest(message: String) extends ClientError
  object BadRequest {
    implicit val codec: Codec[BadRequest] = deriveCodec
  }

  //Naive implementation because it is not exposing the full error Cause
  implicit class mapError[R, E, A](val f: ZIO[R, E, A]) extends AnyVal {
    def mapToClientError: ZIO[R with Logging, ClientError, A] =
      f.tapError(error => log.error(s"Error processing request: $error."))
        .mapError {
          case r: UserNotFound      => BadRequest(r.getMessage)
          case r: ErrorFetchingUser => BadRequest(r.getMessage)
          case r: EmailNotValid     => BadRequest(r.getMessage)
          case _                    => InternalServerError("Internal Server Error")
        }
  }
}
