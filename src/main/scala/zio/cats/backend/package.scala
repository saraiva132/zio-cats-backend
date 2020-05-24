package zio.cats

import zio.cats.backend.persistence.UserPersistence
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.services.user.UserService
import zio.ZEnv
import zio.cats.backend.system.config.Config

package object backend {

  type ServiceEnv = UserService with UserPersistence with ReqResClient with HealthCheck
  type AppEnv     = ServiceEnv with ZEnv with Config

}
