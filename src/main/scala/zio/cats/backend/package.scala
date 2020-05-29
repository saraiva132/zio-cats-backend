package zio.cats

import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import zio._
import zio.cats.backend.persistence.UserPersistence
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.services.user.UserService
import zio.cats.backend.system.config.Config
import zio.logging.Logging
import zio.clock.Clock

package object backend {

  type SttpClientService = SttpBackend[Task, Nothing, WebSocketHandler]
  type UserServiceEnv    = UserService with UserPersistence with ReqResClient with Logging with Clock
  type AppEnv            = UserServiceEnv with ZEnv with Config with HealthCheck

}
