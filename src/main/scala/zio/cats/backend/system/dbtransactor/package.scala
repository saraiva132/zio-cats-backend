package zio.cats.backend.system

import cats.effect.Blocker
import doobie.h2.H2Transactor
import doobie.util.transactor.Transactor
import scala.concurrent.ExecutionContext
import zio.blocking.Blocking
import zio.{Has, Managed, Task, URIO, ZIO, ZLayer, blocking}
import zio.cats.backend.system.config.PostgresConfig
import zio.interop.catz._

package object dbtransactor {

  type DBTransactor = Has[Transactor[Task]]

  object DBTransactor {
    private def makeTransactor(
      conf: PostgresConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
    ): Managed[Throwable, Transactor[Task]] =
      H2Transactor
        .newH2Transactor[Task](
          conf.url.value,
          conf.user.value,
          conf.password.value,
          connectEC,
          Blocker.liftExecutionContext(transactEC)
        )
        .toManagedZIO

    val live: ZLayer[Has[PostgresConfig] with Blocking, Throwable, DBTransactor] =
      ZLayer.fromManaged(
        for {
          _          <- Migration.migrate.toManaged_
          config     <- config.dbConfig.toManaged_
          connectEC  <- ZIO.descriptor.map(_.executor.asEC).toManaged_
          blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
          transactor <- makeTransactor(config, connectEC, blockingEC)
        } yield transactor
      )
  }

  val transactor: URIO[DBTransactor, Transactor[Task]] = ZIO.access(_.get)

}
