package zio.cats.backend

import zio.cats.backend.http.Server
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.system.config
import zio.cats.backend.system.config.Config
import zio.{App, UIO, ZEnv, ZIO}

object Main extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {

    val main = for {
      svConfig <- config.httpServerConfig
      _        <- Server.runServer(svConfig)
    } yield ()

    main
      .provideSomeLayer[ZEnv](Config.live ++ HealthCheck.live)
      .foldM(
        _ => UIO.succeed(1),
        _ => UIO.succeed(0)
      )
  }
}
