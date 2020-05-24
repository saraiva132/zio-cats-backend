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
import zio.{App, UIO, ZEnv, ZIO}

object Main extends App {

  val configLayer          = Logger.live >>> Config.live
  val transactorLayer      = Logger.live ++ Blocking.live ++ configLayer >>> DBTransactor.live
  val httpCLient           = configLayer >>> Client.live
  val userPersistenceLayer = transactorLayer >>> UserPersistenceSQL.live
  val resreqClientLayer    = configLayer ++ httpCLient >>> ReqResClientHTTP.live
  val userManagerLayer     = UserService.live

  val appLayers = Logger.live ++ configLayer ++ HealthCheck.live ++ userPersistenceLayer ++ resreqClientLayer ++ userManagerLayer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .foldM(
        _ => UIO.succeed(1),
        _ => UIO.succeed(0)
      )

}
