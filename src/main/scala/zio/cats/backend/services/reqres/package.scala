package zio.cats.backend.services.reqres

import zio.cats.backend.data.{User, UserId}
import zio.{Has, RIO, Task}

package object reqres {

  type ReqResClient = Has[ReqResClient.Service]

  object ReqResClient {

    trait Service {
      def fetchUser(userId: UserId): Task[User]
    }

    def fetchUser(userId: UserId): RIO[ReqResClient, User] = RIO.accessM(_.get.fetchUser(userId))
  }

}
