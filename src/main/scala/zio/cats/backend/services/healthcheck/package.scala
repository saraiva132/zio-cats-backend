package zio.cats.backend.services

import zio.cats.backend.services.healthcheck.Health.Healthy
import zio.{Has, Layer, RIO, Task, UIO, ZLayer}

package object healthcheck {

  type HealthCheck = Has[HealthCheck.Service]

  object HealthCheck {
    trait Service {
      def healthStatus: Task[Health]
    }
    val live: Layer[Nothing, HealthCheck] = ZLayer.succeed(new HealthCheck.Service {
      override def healthStatus: Task[Health] = UIO.succeed(Healthy)
    })
  }

  val healthStatus: RIO[HealthCheck, Health] = RIO.accessM(_.get.healthStatus)

}
