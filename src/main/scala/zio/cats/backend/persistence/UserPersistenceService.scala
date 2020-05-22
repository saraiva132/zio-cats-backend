package zio.cats.backend.persistence

import cats.effect.Blocker
import doobie.h2.H2Transactor
import doobie.implicits._
import doobie.{Query0, Transactor, Update0}
import doobie.refined.implicits._
import scala.concurrent.ExecutionContext
import zio._
import zio.blocking.Blocking
import zio.cats.backend.Models.{User, UserId, UserNotFound}
import zio.cats.backend.config
import zio.cats.backend.config.PostgresConfig
import zio.interop.catz._

/**
  * Persistence Module for production using Doobie
  */
final class UserPersistenceService(tnx: Transactor[Task]) extends Persistence.Service[UserId, User] {
  import UserPersistenceService._

  def get(userId: UserId): Task[User] =
    Queries
      .get(userId)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeUser => Task.require(UserNotFound(userId))(Task.succeed(maybeUser))
      )

  def create(user: User): Task[User] =
    Queries
      .create(user)
      .run
      .transact(tnx)
      .foldM(err => Task.fail(err), _ => Task.succeed(user))

  def delete(userId: UserId): Task[Boolean] =
    Queries
      .delete(userId)
      .run
      .transact(tnx)
      .fold(_ => false, _ => true)
}

object UserPersistenceService {

  type UserPersistence = Persistence[UserId, User]

  def getUser(userId: UserId): RIO[UserPersistence, User]       = RIO.accessM(_.get.get(userId))
  def createUser(user: User): RIO[UserPersistence, User]        = RIO.accessM(_.get.create(user))
  def deleteUser(userId: UserId): RIO[UserPersistence, Boolean] = RIO.accessM(_.get.delete(userId))

  object Queries {

    def get(userId: UserId): Query0[User] =
      sql"""SELECT * FROM USERS WHERE ID = ${userId.value} """.query[User]

    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (id, email) VALUES (${user.userId.value}, ${user.email.value})""".update

    def delete(userId: UserId): Update0 =
      sql"""DELETE FROM USERS WHERE id = ${userId.value}""".update
  }

  def makeTransactor(
    conf: PostgresConfig,
    connectEC: ExecutionContext,
    transactEC: ExecutionContext
  ): Managed[Throwable, UserPersistenceService] = {
    import zio.interop.catz._

    H2Transactor
      .newH2Transactor[Task](
        conf.url.value,
        conf.user.value,
        conf.password.value,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO
      .map(new UserPersistenceService(_))
  }

  val live: ZLayer[Has[PostgresConfig] with Blocking, Throwable, UserPersistence] =
    ZLayer.fromManaged(
      for {
        config     <- config.dbConfig.toManaged_
        connectEC  <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
        managed    <- makeTransactor(config, connectEC, blockingEC)
      } yield managed
    )

}
