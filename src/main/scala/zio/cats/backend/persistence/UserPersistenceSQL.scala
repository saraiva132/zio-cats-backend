package zio.cats.backend.persistence

import doobie.implicits._
import doobie.refined.implicits._
import doobie.util.transactor.Transactor
import doobie.{Query0, Update0}

import zio._
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.system.dbtransactor
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.interop.catz._

/**
  * Persistence Module for production using Doobie
  */
final class UserPersistenceSQL(tnx: Transactor[Task]) extends Persistence.Service[UserId, User] {
  import UserPersistenceSQL._

  def retrieve(userId: UserId): Task[Option[User]] =
    Queries
      .get(userId)
      .option
      .transact(tnx)

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

  def isHealthy: Task[Boolean] =
    Queries.health.option
      .transact(tnx)
      .fold(_ => false, _ => true)
}

object UserPersistenceSQL {

  type UserPersistence = Persistence[UserId, User]

  def retrieve(userId: UserId): RIO[UserPersistence, Option[User]] = RIO.accessM(_.get.retrieve(userId))
  def create(user: User): RIO[UserPersistence, User]               = RIO.accessM(_.get.create(user))
  def delete(userId: UserId): RIO[UserPersistence, Boolean]        = RIO.accessM(_.get.delete(userId))
  def isHealthy: RIO[UserPersistence, Boolean]                     = RIO.accessM(_.get.isHealthy)

  object Queries {

    def get(userId: UserId): Query0[User] =
      sql"""SELECT * FROM USERS WHERE ID = ${userId.value} """.query[User]

    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (id, email) VALUES (${user.userId.value}, ${user.email.value})""".update

    def delete(userId: UserId): Update0 =
      sql"""DELETE FROM USERS WHERE id = ${userId.value}""".update

    val health: Query0[Unit] = sql"""SELECT 1 as one;""".query[Unit]

  }

  val live: ZLayer[DBTransactor, Throwable, UserPersistence] =
    ZLayer.fromEffect(
      dbtransactor.transactor.map(new UserPersistenceSQL(_))
    )

}
