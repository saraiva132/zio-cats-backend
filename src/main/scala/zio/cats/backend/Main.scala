package zio.cats.backend

import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.blocking.Blocking
import zio.cats.backend.services.reqres.ReqResClient
import zio.cats.backend.http.Server
import zio.cats.backend.system.logging.Logger
import zio.cats.backend.persistence.UserPersistenceSQL
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.user.UserManager
import zio.cats.backend.system.config.Config
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.logging.log
import zio.{App, UIO, ZEnv, ZIO}

object Main extends App {

  val transactorLayer      = Blocking.live ++ Config.live >>> DBTransactor.live
  val userPersistenceLayer = transactorLayer >>> UserPersistenceSQL.live
  val resreqClientLayer    = Config.live ++ AsyncHttpClientZioBackend.layer() >>> ReqResClient.live
  val userManagerLayer     = UserManager.live

  val appLayers = Logger.live ++ Config.live ++ HealthCheck.live ++ userPersistenceLayer ++ resreqClientLayer ++ userManagerLayer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .foldM(
        _ => UIO.succeed(1),
        _ => UIO.succeed(0)
      )

}
