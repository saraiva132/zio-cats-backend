package zio.cats.backend.system.config

import eu.timepit.refined.types.string.NonEmptyString

final case class PostgresConfig(
  url: NonEmptyString,
  user: NonEmptyString,
  password: NonEmptyString
)
