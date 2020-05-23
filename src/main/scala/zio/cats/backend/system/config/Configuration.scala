package zio.cats.backend.system.config

final case class Configuration(
  httpServer: HttpServerConfig,
  httpClient: HttpClientConfig,
  dbConfig: PostgresConfig,
  reqResClient: ReqResConfig
)
