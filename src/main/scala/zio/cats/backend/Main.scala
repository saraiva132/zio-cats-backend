package zio.cats.backend

import zio.blocking.Blocking
import zio.cats.backend.http.{Client, Server}
import zio.cats.backend.persistence.UserPersistenceSQL
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.reqres.ReqResClientHTTP
import zio.cats.backend.services.user.UserService
import zio.cats.backend.system.config.Config
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.cats.backend.system.logging.Logger
import zio.logging.log
import zio.{App, ExitCode, ZEnv, ZIO}

object Main extends App {

  val logger          = Logger.live
  val config          = logger >>> Config.live
  val transactor      = logger ++ Blocking.any ++ config >>> DBTransactor.live
  val httpClient      = config >>> Client.live
  val userPersistence = transactor >>> UserPersistenceSQL.live
  val resReqClient    = config ++ httpClient >>> ReqResClientHTTP.live
  val userManager     = UserService.live

  val appLayers = logger ++ config ++ HealthCheck.live ++ userPersistence ++ resReqClient ++ userManager

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .exitCode
}
