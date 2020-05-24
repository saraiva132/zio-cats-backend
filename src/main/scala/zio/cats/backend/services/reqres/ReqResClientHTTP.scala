package zio.cats.backend.services.reqres

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.circe._
import sttp.model.{StatusCode, Uri}

import zio._
import zio.cats.backend.data.Error.{ErrorFetchingUser, UserNotFound}
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.services.reqres.ReqResClientHTTP.ReqResResponse
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.system.config.{Config, ReqResConfig}

final class ReqResClientHTTP(
  client: SttpBackend[Task, Nothing, WebSocketHandler],
  config: ReqResConfig
) extends ReqResClient.Service {

  private val endpoint = Uri(config.protocol.value, config.host.value, config.port.value)
  private val basePath = "/api/users"

  override def fetchUser(userId: UserId): Task[User] = {

    val path = s"$basePath/${userId.value}"

    client
      .send(
        basicRequest
          .response(asJson[ReqResResponse])
          .get(endpoint.path(path))
      )
      .reject {
        case r if r.code == StatusCode.NotFound => UserNotFound(userId)
      }
      .map(_.body)
      .flatMap(e => IO.fromEither(e).mapError(err => ErrorFetchingUser(userId, err.body)))
      .map(_.data)
  }

}

object ReqResClientHTTP {

  final case class ReqResResponse(data: User)
  object ReqResResponse {
    implicit val codec: Codec[ReqResResponse] = deriveCodec
  }

  val live: URLayer[Has[ReqResConfig] with SttpClient, ReqResClient] =
    ZLayer.fromEffect(
      for {
        client <- ZIO.access[SttpClient](_.get)
        config <- Config.reqResConfig
      } yield new ReqResClientHTTP(client, config)
    )
}
