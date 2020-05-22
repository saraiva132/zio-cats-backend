package zio.cats.backend.config

import eu.timepit.refined.types.string.NonEmptyString

final case class PostgresConfig(
  url: NonEmptyString,
  user: NonEmptyString,
  password: NonEmptyString
)
