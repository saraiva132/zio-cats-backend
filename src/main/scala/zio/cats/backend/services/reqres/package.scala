package zio.cats.backend.services.reqres

import zio.cats.backend.data.{User, UserId}
import zio.{Has, RIO, Task}

package object reqres {
  type ReqRes = Has[ReqRes.Service]

  object ReqRes {
    trait Service {
      def fetchUser(userId: UserId): Task[User]
    }

    def fetchUser(userId: UserId): RIO[ReqRes, User] = RIO.accessM(_.get.fetchUser(userId))
  }

}
