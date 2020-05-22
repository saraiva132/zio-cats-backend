package zio.cats.backend.config

final case class Configuration(
  httpServer: HttpServerConfig,
  httpClient: HttpClientConfig,
  dbConfig: PostgresConfig
)
