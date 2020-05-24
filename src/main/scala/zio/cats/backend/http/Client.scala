package zio.cats.backend.http

import sttp.client.SttpBackendOptions
import sttp.client.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}

import zio.cats.backend.system.config.{Config, HttpClientConfig}
import zio.{Has, ZLayer}

object Client {

  val live: ZLayer[Has[HttpClientConfig], Throwable, SttpClient] =
    ZLayer.fromManaged(
      for {
        config <- Config.httpClientConfig.toManaged_
        options = SttpBackendOptions(config.connectingTimeout, None)
        client <- AsyncHttpClientZioBackend.managed(options)
      } yield client
    )

}
