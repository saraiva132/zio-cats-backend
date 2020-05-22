package zio.cats.backend.services

import zio.cats.backend.services.healthcheck.Health.{Healthy, Unhealthy}
import zio.{Has, RIO, ULayer, ZLayer}
import zio.cats.backend.persistence.UserPersistenceSQL
import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence

package object healthcheck {

  type HealthCheck = Has[HealthCheck.Service]

  object HealthCheck {
    trait Service {
      def healthStatus: RIO[UserPersistence, Health]
    }

    val live: ULayer[HealthCheck] = ZLayer.succeed(
      new Service {
        override def healthStatus: RIO[UserPersistence, Health] =
          UserPersistenceSQL.isHealthy.map {
            case true  => Healthy
            case false => Unhealthy
          }
      }
    )
  }

  val healthStatus: RIO[HealthCheck with UserPersistence, Health] = RIO.accessM(_.get.healthStatus)

}
