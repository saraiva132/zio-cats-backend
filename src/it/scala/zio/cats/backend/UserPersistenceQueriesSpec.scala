package zio.cats.backend

import zio.test.{DefaultRunnableSpec, suite, testM}
import zio.cats.backend.testkit.ZIOChecker
import zio.cats.backend.persistence.UserPersistenceSQL.Queries
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.Runtime
import zio.ZManaged.ReleaseMap
import zio.blocking.Blocking
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.system.config.Config
import zio.cats.backend.system.logging.Logger

object UserPersistenceQueriesSpec extends DefaultRunnableSpec with ZIOChecker {

  val runtime     = Runtime.default //Use test runtime
  val releaseMap  = runtime.unsafeRun(ReleaseMap.make)
  val configLayer = Logger.test >>> Config.live ++ Blocking.live
  val finalLayer  = (configLayer ++ Logger.test).map(cfg => (cfg, releaseMap))

  val transactorF = DBTransactor.managed.zio.provideLayer(finalLayer)
  val transactor  = runtime.unsafeRun(transactorF)._2

  def spec =
    suite("Validate queries")(
      testM("Check get query")(check(Queries.get(UserId.Test))),
      testM("Check create query")(check(Queries.create(User.Test))),
      testM("Check delete query")(check(Queries.delete(UserId.Test)))
    )

}
