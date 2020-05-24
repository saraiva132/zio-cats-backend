package zio.cats.backend.system.config

import scala.concurrent.duration._

import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

final case class HttpServerConfig(
  host: NonEmptyString,
  port: UserPortNumber,
  poolSize: PosInt = HttpServerConfig.defaultPoolSize,
  responseHeaderTimeout: FiniteDuration = 10.seconds,
  idleTimeout: FiniteDuration = 30.seconds
)

object HttpServerConfig {
  private val cores = Runtime.getRuntime.availableProcessors()
  val defaultPoolSize = PosInt.unsafeFrom(
    math.max(4, cores + 1)
  )
}
