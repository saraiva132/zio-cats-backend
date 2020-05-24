package zio.cats.backend.system.config

import eu.timepit.refined.types.string.NonEmptyString

final case class PostgresConfig(
  className: NonEmptyString,
  url: NonEmptyString,
  user: NonEmptyString,
  password: NonEmptyString
)
