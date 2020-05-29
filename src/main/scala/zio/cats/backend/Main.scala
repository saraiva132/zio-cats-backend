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

  val configLayer          = Logger.live >>> Config.live
  val transactorLayer      = Logger.live ++ Blocking.any ++ configLayer >>> DBTransactor.live
  val httpClient           = configLayer >>> Client.live
  val userPersistenceLayer = transactorLayer >>> UserPersistenceSQL.live
  val resReqClientLayer    = configLayer ++ httpClient >>> ReqResClientHTTP.live
  val userManagerLayer     = UserService.live

  val appLayers = Logger.live ++ configLayer ++ HealthCheck.live ++ userPersistenceLayer ++ resReqClientLayer ++ userManagerLayer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .exitCode
}
