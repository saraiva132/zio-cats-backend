package zio.cats.backend.services

import zio.{Has, RIO, Task}

package object healthcheck {

  object HealthCheck {
    trait Service {
      def healthStatus: Task[Health]
    }
  }

  type HealthCheck = Has[HealthCheck.Service]

  val healthStatus: RIO[HealthCheck, Health] = RIO.accessM(_.get.healthStatus)

}
