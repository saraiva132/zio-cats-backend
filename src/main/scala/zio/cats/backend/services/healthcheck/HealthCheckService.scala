package zio.cats.backend.services.healthcheck

import zio.cats.backend.services.healthcheck.Health.Healthy
import zio.{Task, UIO}

final class HealthCheckService extends HealthCheck.Service {
  override def healthStatus: Task[Health] = UIO.succeed(Healthy)
}
