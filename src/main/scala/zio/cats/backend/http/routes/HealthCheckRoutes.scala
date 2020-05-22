package zio.cats.backend.http.routes

import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.cats.backend.services.healthcheck.Health._
import zio.cats.backend.services.healthcheck.{HealthCheck, _}
import zio.interop.catz._
import zio._

final class HealthCheckRoutes {

  private val healthCheck: ZIO[HealthCheck, String, String] =
    healthStatus.orElseFail("FAILURE").collectM("FAILURE") {
      case Healthy      => UIO.succeed("Healthy!")
      case Unhealthy    => IO.fail("FAILURE")
      case ShuttingDown => IO.fail("SHUTTING DOWN")
    }

  private val aliveEndpoint: ZEndpoint[Unit, String, String] =
    endpoint.get.in("health" / "alive").errorOut(stringBody).out(jsonBody[String])

  private val readyEndpoint: ZEndpoint[Unit, String, String] =
    endpoint.get.in("health" / "ready").errorOut(stringBody).out(jsonBody[String])

  private val aliveRoute: URIO[HealthCheck, HttpRoutes[Task]] = aliveEndpoint.toRoutesR(_ => healthCheck)
  private val readyRoute: URIO[HealthCheck, HttpRoutes[Task]] = readyEndpoint.toRoutesR(_ => healthCheck)

  val routes: URIO[HealthCheck, HttpRoutes[Task]] = for {
    aliveRoute <- aliveRoute
    readyRoute <- readyRoute
  } yield aliveRoute <+> readyRoute
}
