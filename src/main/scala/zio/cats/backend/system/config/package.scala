package zio.cats.backend.system

import pureconfig.ConfigSource
import zio._
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._

package object config {

  type Config = Has[HttpServerConfig] with Has[HttpClientConfig] with Has[PostgresConfig]

  object Config {

    private val basePath = "zio.cats.backend"
    private val source   = ConfigSource.default.at(basePath)

    val live: Layer[Throwable, Config] = ZLayer.fromEffectMany(
      Task
        .effect(source.loadOrThrow[Configuration])
        .map(c => Has(c.httpServer) ++ Has(c.httpClient) ++ Has(c.dbConfig))
    )
  }

  val httpServerConfig: URIO[Has[HttpServerConfig], HttpServerConfig] = ZIO.access(_.get)
  val httpClientConfig: URIO[Has[HttpClientConfig], HttpClientConfig] = ZIO.access(_.get)
  val dbConfig: URIO[Has[PostgresConfig], PostgresConfig]             = ZIO.access(_.get)

}
