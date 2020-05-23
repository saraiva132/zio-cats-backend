package zio.cats.backend.system.config

import scala.concurrent.duration._

import eu.timepit.refined.types.numeric.PosInt

final case class HttpClientConfig(
  maxConnections: PosInt = HttpClientConfig.defaultMaxConnections,
  connectingTimeout: FiniteDuration = 30.seconds,
  requestTimeout: FiniteDuration = 10.seconds,
  idleTimeout: FiniteDuration = 30.seconds
)

object HttpClientConfig {

  val defaultMaxConnections = PosInt.unsafeFrom(256)

}
