package zio.cats.backend.services.reqres

import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.circe._
import zio.cats.backend.system.config
import zio.cats.backend.system.config.ReqResConfig
import zio.cats.backend.data.Error.ErrorFetchingUser
import zio.cats.backend.data.{User, UserId}
import zio._
import zio.cats.backend.services.reqres.reqres.ReqRes

final class ReqResClient(client: SttpBackend[Task, Nothing, WebSocketHandler], config: ReqResConfig) extends ReqRes.Service {

  private val endpoint = s"${config.host.value}:${config.port.value}"
  private val path     = "/api/users"

  override def fetchUser(userId: UserId): Task[User] =
    client
      .send(
        basicRequest
          .response(asJson[User])
          .get(uri"$endpoint/$path/${userId.value}")
      )
      .map(_.body)
      .flatMap(e => IO.fromEither(e).mapError(err => ErrorFetchingUser(userId, err.body)))

}

object ReqResClient {

  val live: URLayer[Has[ReqResConfig] with SttpClient, ReqRes] =
    ZLayer.fromEffect(
      for {
        client <- ZIO.environment[SttpClient]
        config <- config.reqResConfig
      } yield new ReqResClient(client.get, config)
    )

}
