package zio.cats.backend.system.config

import eu.timepit.refined.types.net.PortNumber
import eu.timepit.refined.types.string.NonEmptyString

final case class ReqResConfig(
  protocol: NonEmptyString,
  host: NonEmptyString,
  port: PortNumber
)
