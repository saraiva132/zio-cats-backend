package zio.cats.backend.services

import zio.{Has, RIO, Task, UIO, ULayer, ZLayer}
import zio.cats.backend.data.{PostUser, User, UserId}
import zio.cats.backend.persistence.UserPersistenceSQL._
import zio.cats.backend.services.reqres.reqres._
import zio.cats.backend.data.Error.UserNotFound

package object user {

  type UserManager = Has[UserManager.Service]

  object UserManager {
    trait Service {
      def registerUser(postUser: PostUser): RIO[UserPersistence with ReqRes, Unit]
      def getUser(userId: UserId): RIO[UserPersistence, User]
      def deleteUser(userId: UserId): RIO[UserPersistence, Unit]
    }

    val live: ULayer[UserManager] =
      ZLayer.succeed(
        new UserManager.Service {
          override def registerUser(postUser: PostUser): RIO[UserPersistence with ReqRes, Unit] =
            for {
              user <- fetchUser(postUser.userId)
              _    <- create(user)
            } yield ()

          override def getUser(userId: UserId): RIO[UserPersistence, User] =
            retrieve(userId)
              .flatMap(maybeUser => Task.require(UserNotFound(userId))(Task.succeed(maybeUser)))

          override def deleteUser(userId: UserId): RIO[UserPersistence, Unit] =
            delete(userId).flatMap {
              case true  => UIO.unit
              case false => Task.fail(UserNotFound(userId))
            }
        }
      )
  }

  def registerUser(postUser: PostUser): RIO[UserManager with UserPersistence with ReqRes, Unit] =
    RIO.accessM(_.get.registerUser(postUser))

  def getUser(userId: UserId): RIO[UserManager with UserPersistence, User] =
    RIO.accessM(_.get.getUser(userId))

  def deleteUser(userId: UserId): RIO[UserManager with UserPersistence, Unit] =
    RIO.accessM(_.get.deleteUser(userId))

}
