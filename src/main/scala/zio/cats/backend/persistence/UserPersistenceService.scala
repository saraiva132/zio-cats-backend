package zio.cats.backend.persistence

import doobie.implicits._
import doobie.{Query0, Update0}
import doobie.refined.implicits._
import doobie.util.transactor.Transactor
import zio._
import zio.cats.backend.{User, UserId, UserNotFound}
import zio.interop.catz._
import zio.cats.backend.system.dbtransactor
import zio.cats.backend.system.dbtransactor.DBTransactor

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

  val live: ZLayer[DBTransactor, Throwable, UserPersistence] =
    ZLayer.fromEffect(
      dbtransactor.transactor.map(new UserPersistenceService(_))
    )

}
