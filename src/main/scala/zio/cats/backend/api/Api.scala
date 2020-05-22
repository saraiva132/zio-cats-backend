package zio.cats.backend.api

import cats.effect.Blocker
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import zio.cats.backend.config.HttpServerConfig
import zio.cats.backend.services.healthcheck._
import zio.{Runtime, Task, URIO, ZIO}
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import org.http4s.implicits._
import zio.clock.Clock
import zio.interop.catz.implicits._

final class Api() extends Http4sDsl[Task[*]] {

  val healthCheckRoutes                     = new HealthCheckRoutes().routes
  val api: URIO[HealthCheck, HttpApp[Task]] = healthCheckRoutes.map(_.orNotFound)
}

object Api {
  def makeServer(blocker: Blocker, cfg: HttpServerConfig): ZIO[Clock with HealthCheck, Throwable, Unit] = {
    val api = new Api()

    for {
      api                                <- api.api
      implicit0(runtime: Runtime[Clock]) <- ZIO.runtime[Clock]
      _ <-
        BlazeServerBuilder[Task](blocker.blockingContext)
          .bindHttp(cfg.port.value, cfg.host.value)
          .withoutBanner
          .withNio2(true)
          .withHttpApp(api)
          .serve
          .compile
          .drain
    } yield ()

  }
}
