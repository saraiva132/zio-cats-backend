package zio.cats.backend.services.reqres

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.client._
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.circe._
import sttp.model.{StatusCode, Uri}

import zio._
import zio.cats.backend.SttpClientService
import zio.cats.backend.data.Error.{ErrorFetchingUser, RequestTimedOut, UserNotFound}
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.services.reqres.ReqResClientHTTP.ReqResResponse
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.system.config.{Config, ReqResConfig}
import zio.clock.Clock
import zio.duration._

final class ReqResClientHTTP(
  client: SttpClientService,
  config: ReqResConfig
) extends ReqResClient.Service {

  private val endpoint  = Uri(config.protocol.value, config.host.value, config.port.value)
  private val basePath  = "/api/users"
  private val scheduler = Schedule.exponential(50.millis) && Schedule.recurs(10)

  override def fetchUser(userId: UserId): RIO[Clock, User] = {

    val path = s"$basePath/${userId.value}"
    val request = basicRequest
      .response(asJson[ReqResResponse])
      .get(endpoint.path(path))

    client
      .send(request)
      .retry(scheduler)
      .timeoutFail(RequestTimedOut(s"Request to ReqRes for userId $userId."))(30.seconds)
      .reject {
        case r if r.code == StatusCode.NotFound => UserNotFound(userId)
      }
      .map(_.body)
      .absolve
      .bimap(err => ErrorFetchingUser(userId, err.getMessage), _.data)
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
        client <- ZIO.service[SttpClientService]
        config <- Config.reqResConfig
      } yield new ReqResClientHTTP(client, config)
    )
}
