package zio.cats.backend.system

import scala.concurrent.ExecutionContext

import cats.effect.Blocker

import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import zio.blocking.Blocking
import zio.cats.backend.system.config.{Config, PostgresConfig}
import zio.interop.catz._
import zio.logging.{Logging, log}
import zio.{Has, Managed, Task, URIO, ZIO, ZLayer, ZManaged, blocking}

package object dbtransactor {

  type DBTransactor = Has[Transactor[Task]]

  object DBTransactor {
    private def makeTransactor(
      conf: PostgresConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
    ): Managed[Throwable, Transactor[Task]] =
      HikariTransactor
        .newHikariTransactor[Task](
          conf.className.value,
          conf.url.value,
          conf.user.value,
          conf.password.value,
          connectEC,
          Blocker.liftExecutionContext(transactEC)
        )
        .toManagedZIO

    val managed: ZManaged[Has[PostgresConfig] with Blocking with Logging, Throwable, Transactor[Task]] =
      for {
        config     <- Config.dbConfig.toManaged_
        connectEC  <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
        _          <- log.info("Initializing DB Transactor and creating connection pool.").toManaged_
        transactor <- makeTransactor(config, connectEC, blockingEC)
      } yield transactor

    val managedWithMigration: ZManaged[Has[PostgresConfig] with Logging with Blocking, Throwable, Transactor[Task]] =
      Migration.migrate.toManaged_ *> managed

    val test: ZLayer[Has[PostgresConfig] with Blocking with Logging, Throwable, DBTransactor] =
      ZLayer.fromManaged(managed)

    val live: ZLayer[Has[PostgresConfig] with Logging with Blocking, Throwable, DBTransactor] =
      ZLayer.fromManaged(managedWithMigration)

    val transactor: URIO[DBTransactor, Transactor[Task]] = ZIO.service

  }
}
