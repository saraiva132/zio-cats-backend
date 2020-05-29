package zio.cats.backend.system

import scala.jdk.CollectionConverters._

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import zio.logging.{Logging, log}
import zio.{Task, _}

/**
  * Config loading in ZIO has two approaches to it that I see.
  *
  * 1.   Provide a type alias using type intersection similar to how it is used here.
  *      I.e. type Config = Has[HttpServerConfig] with Has[HttpClientConfig]
  *      expose access to the inner parts of the config
  *
  * 2.   Create a minimalist service with a method load / loadConfig
  *      type ConfigService = Has[ConfigService.Service]
  *
  *      object ConfigService {
  *
  *      trait Service {
  *        def loadConfig: UIO[Config]
  *      }
  *
  *      val live: ZLayer[Any, Nothing, ConfigService] =
  *        ZLayer.succeed {
  *          new Service {
  *            def loadConfig: UIO[Config] = ???
  *          }
  *        }
  *      }
  *
  *      Expose the inner parts of the Config using either access methods or type alias
  *
  *      type PostgresConfigService = Has[PostgresConfig]
  *
  *      object PostgresConfigService {
  *      val live: ZLayer[ConfigService, Nothing, PostgresConfigService] =
  *        ZLayer.fromServiceM { (config: ConfigService.Service) =>
  *          config.loadConfig.map(_.dbConfig)
  *        }
  *      }
  *
  */
package object config {

  type Config = Has[HttpServerConfig] with Has[HttpClientConfig] with Has[PostgresConfig] with Has[ReqResConfig]

  object Config {

    private val basePath = "zio.cats.backend"
    private val source   = ConfigSource.default.at(basePath)

    private val buildEnv: Task[String] =
      Task.effect {
        System
          .getenv()
          .asScala
          .map(v => s"${v._1} = ${v._2}")
          .mkString("\n", "\n", "")
      }

    private def logEnv(ex: Throwable): ZIO[Logging, Throwable, Unit] =
      for {
        env <- buildEnv
        _   <- log.error(s"Loading configuration failed with the following environment variables: $env.")
        _   <- log.error(s"Error thrown was $ex.")
      } yield ()

    val live: ZLayer[Logging, Throwable, Config] = ZLayer.fromEffectMany(
      Task
        .effect(source.loadOrThrow[Configuration])
        .map(c => Has(c.httpServer) ++ Has(c.httpClient) ++ Has(c.dbConfig) ++ Has(c.reqResClient))
        .tapError(logEnv)
    )

    val httpServerConfig: URIO[Has[HttpServerConfig], HttpServerConfig] = ZIO.service
    val httpClientConfig: URIO[Has[HttpClientConfig], HttpClientConfig] = ZIO.service
    val dbConfig: URIO[Has[PostgresConfig], PostgresConfig]             = ZIO.service
    val reqResConfig: URIO[Has[ReqResConfig], ReqResConfig]             = ZIO.service
  }
}
