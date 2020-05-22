package zio.cats

import zio.cats.backend.services.reqres.reqres.ReqRes
import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence
import zio.cats.backend.services.healthcheck.HealthCheck
import zio.cats.backend.services.user.UserManager

package object backend {

  type AppEnv = UserManager with UserPersistence with ReqRes with HealthCheck

}
