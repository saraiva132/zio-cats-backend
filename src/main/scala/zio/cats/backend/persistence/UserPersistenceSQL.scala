package zio.cats.backend.persistence

import doobie.implicits._
import doobie.refined.implicits._
import doobie.util.transactor.Transactor
import doobie.{Query0, Update0}

import zio._
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.persistence.UserPersistenceSQL.Queries
import zio.cats.backend.system.dbtransactor.DBTransactor
import zio.interop.catz._

/**
  * Persistence Module for production using Doobie
  */
final class UserPersistenceSQL(trx: Transactor[Task]) extends UserPersistence.Service {

  def retrieve(userId: UserId): Task[Option[User]] =
    Queries
      .get(userId)
      .option
      .transact(trx)

  def create(user: User): Task[User] =
    Queries
      .create(user)
      .run
      .transact(trx)
      .foldM(err => Task.fail(err), _ => Task.succeed(user))

  def delete(userId: UserId): Task[Boolean] =
    Queries
      .delete(userId)
      .run
      .transact(trx)
      .fold(_ => false, _ => true)

  def isHealthy: Task[Boolean] =
    Queries.health.option
      .transact(trx)
      .fold(_ => false, _ => true)
}

object UserPersistenceSQL {

  object Queries {
    def get(userId: UserId): Query0[User] =
      sql"""SELECT * FROM USERS WHERE ID = ${userId.value} """.query[User]

    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (id, email, first_name, last_name) VALUES (${user.id.value}, ${user.email.value}, ${user.first_name.value}, ${user.last_name.value})""".update

    def delete(userId: UserId): Update0 =
      sql"""DELETE FROM USERS WHERE id = ${userId.value}""".update

    val health: Query0[Unit] = sql"""SELECT 1 as one;""".query[Unit]
  }

  val live: ZLayer[DBTransactor, Throwable, UserPersistence] =
    ZLayer.fromEffect(
      DBTransactor.transactor.map(new UserPersistenceSQL(_))
    )

}
