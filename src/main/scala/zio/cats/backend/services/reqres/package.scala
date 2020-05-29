package zio.cats.backend.services.reqres

import zio.cats.backend.data.{User, UserId}
import zio.clock.Clock
import zio.{Has, RIO}

package object reqres {

  type ReqResClient = Has[ReqResClient.Service]

  object ReqResClient {

    trait Service {
      def fetchUser(userId: UserId): RIO[Clock, User]
    }

    def fetchUser(userId: UserId): RIO[ReqResClient with Clock, User] = RIO.accessM(_.get.fetchUser(userId))
  }

}
