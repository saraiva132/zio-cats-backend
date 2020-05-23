package zio.cats

import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.reqres.reqres.ReqRes
import zio.cats.backend.services.user.UserService
import zio.ZEnv
import zio.cats.backend.system.config.Config

package object backend {

  type ServiceEnv = UserService with UserPersistence with ReqRes with HealthCheck
  type AppEnv     = ServiceEnv with ZEnv with Config

}
