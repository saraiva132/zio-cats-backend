package zio.cats.backend

import doobie.util.query.Query0
import zio.{Has, UIO, ZIO, ZLayer}
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.clock.Clock
import doobie.implicits._
import zio.interop.catz._
import zio.duration._

/**
  * Experimenting with using layers to create before/after behaviour
  */
object BeforeAfter {
  type dummyService = Has[Unit]

  private val clearTable: Query0[Unit] = sql"""TRUNCATE USERS;""".query[Unit]

  private val acquire: ZIO[DBTransactor with Clock, Throwable, Unit] =
    for {
      trx <- ZIO.access[DBTransactor](_.get)
      _   <- clearTable.unique.transact(trx).delay(50.millis).eventually.timeout(1.seconds)
    } yield ()

  val live: ZLayer[DBTransactor with Clock, Throwable, dummyService] =
    ZLayer.fromAcquireRelease(acquire)(_ => UIO.unit)
}
