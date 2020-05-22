package zio.cats.backend.services.healthcheck

sealed trait Health

object Health {
  final case object Healthy      extends Health
  final case object Unhealthy    extends Health
  final case object ShuttingDown extends Health
}
