package zio.cats.backend.http

import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import zio.cats.backend.system.config.HttpServerConfig
import zio.{Has, Runtime, Task, URIO, ZIO, blocking}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import zio.blocking.Blocking
import zio.cats.backend.AppEnv
import zio.cats.backend.http.routes.{HealthCheckRoutes, UserRoutes}
import zio.cats.backend.system.config
import zio.clock.Clock
import zio.interop.catz.implicits._
import zio.interop.catz._
import cats.implicits._

final class Server() extends Http4sDsl[Task[*]] {

  val api: URIO[AppEnv, HttpApp[Task]] =
    for {
      userRoutes        <- new UserRoutes().routes
      healthCheckRoutes <- new HealthCheckRoutes().routes
    } yield (userRoutes <+> healthCheckRoutes).orNotFound
}

object Server {
  val runServer: ZIO[Has[HttpServerConfig] with AppEnv with Clock with Blocking, Throwable, Unit] =
    for {
      api                                <- new Server().api
      svConfig                           <- config.httpServerConfig
      implicit0(runtime: Runtime[Clock]) <- ZIO.runtime[Clock]
      bec                                <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC))
      _ <-
        BlazeServerBuilder[Task](bec)
          .bindHttp(svConfig.port.value, svConfig.host.value)
          .withoutBanner
          .withNio2(true)
          .withHttpApp(api)
          .serve
          .compile
          .drain
    } yield ()

}
