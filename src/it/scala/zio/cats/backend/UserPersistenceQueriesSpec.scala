package zio.cats.backend

import zio.test.{DefaultRunnableSpec, suite, testM}
import zio.test.TestAspect._
import zio.cats.backend.testkit.QueryChecker
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.{Exit, Runtime}
import zio.ZManaged.ReleaseMap
import zio.blocking.Blocking
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.persistence.UserPersistenceSQL.Queries
import zio.cats.backend.system.config.Config
import zio.cats.backend.system.logging.Logger
import zio.duration._

object UserPersistenceQueriesSpec extends DefaultRunnableSpec with QueryChecker {

  /**
    * Hack to get doobie query check to work with ZIO
    */
  val runtime    = Runtime.default //Use test runtime
  val releaseMap = runtime.unsafeRun(ReleaseMap.make)

  val logger      = Logger.test
  val configLayer = logger >>> Config.live
  val testLayers  = (configLayer ++ Blocking.live ++ logger).map(cfg => (cfg, releaseMap))

  val transactorF             = DBTransactor.managed.zio.provideLayer(testLayers)
  val (finalizer, transactor) = runtime.unsafeRun(transactorF)

  def spec =
    suite("Validate queries")(
      testM("Check get query")(check(Queries.get(UserId.Test))),
      testM("Check create query")(check(Queries.create(User.Test))),
      testM("Check delete query")(check(Queries.delete(UserId.Test)))
    ) @@ after(finalizer(Exit.unit)) @@ timeout(15 seconds)

}
