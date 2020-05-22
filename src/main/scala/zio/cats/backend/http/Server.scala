package zio.cats.backend.http

import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import zio.cats.backend.system.config.HttpServerConfig
import zio.cats.backend.services.healthcheck._
import zio.{Runtime, Task, URIO, ZIO, blocking}
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import org.http4s.implicits._
import zio.blocking.Blocking
import zio.cats.backend.http.routes.HealthCheckRoutes
import zio.clock.Clock
import zio.interop.catz.implicits._

final class Server() extends Http4sDsl[Task[*]] {

  val healthCheckRoutes                     = new HealthCheckRoutes().routes
  val api: URIO[HealthCheck, HttpApp[Task]] = healthCheckRoutes.map(_.orNotFound)
}

object Server {
  def runServer(cfg: HttpServerConfig): ZIO[Clock with HealthCheck with Blocking, Throwable, Unit] =
    for {
      api                                <- new Server().api
      implicit0(runtime: Runtime[Clock]) <- ZIO.runtime[Clock]
      bec                                <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC))
      _ <-
        BlazeServerBuilder[Task](bec)
          .bindHttp(cfg.port.value, cfg.host.value)
          .withoutBanner
          .withNio2(true)
          .withHttpApp(api)
          .serve
          .compile
          .drain
    } yield ()

}
