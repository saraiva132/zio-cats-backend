package zio.cats.backend.services

import zio.cats.backend.data.Error.UserNotFound
import zio.cats.backend.data.{PostUser, User, UserId}
import zio.cats.backend.persistence.UserPersistenceSQL._
import zio.cats.backend.services.reqres.reqres._
import zio.{Has, RIO, Task, UIO, ULayer, ZLayer}

package object user {

  type UserService = Has[UserService.Service]

  object UserService {
    trait Service {
      def createUser(postUser: PostUser): RIO[UserPersistence with ReqRes, Unit]
      def getUser(userId: UserId): RIO[UserPersistence, User]
      def deleteUser(userId: UserId): RIO[UserPersistence, Unit]
    }

    val live: ULayer[UserService] =
      ZLayer.succeed(
        new UserService.Service {
          override def createUser(postUser: PostUser): RIO[UserPersistence with ReqRes, Unit] =
            for {
              user <- ReqRes.fetchUser(postUser.userId)
              _    <- UserPersistence.create(user)
            } yield ()

          override def getUser(userId: UserId): RIO[UserPersistence, User] =
            UserPersistence
              .retrieve(userId)
              .flatMap(maybeUser => Task.require(UserNotFound(userId))(Task.succeed(maybeUser)))

          override def deleteUser(userId: UserId): RIO[UserPersistence, Unit] =
            UserPersistence.delete(userId).flatMap {
              case true  => UIO.unit
              case false => Task.fail(UserNotFound(userId))
            }
        }
      )

    def registerUser(postUser: PostUser): RIO[UserService with ReqRes with UserPersistence with ReqRes, Unit] =
      RIO.accessM(_.get.createUser(postUser))

    def getUser(userId: UserId): RIO[UserService with UserPersistence, User] =
      RIO.accessM(_.get.getUser(userId))

    def deleteUser(userId: UserId): RIO[UserService with UserPersistence, Unit] =
      RIO.accessM(_.get.deleteUser(userId))
  }
}
