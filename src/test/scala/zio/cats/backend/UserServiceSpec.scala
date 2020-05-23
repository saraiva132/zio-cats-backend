package zio.cats.backend

import zio.ULayer
import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.mocks.UserPersistenceMock
import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence
import zio.cats.backend.services.user.UserService
import zio.test.{DefaultRunnableSpec, suite, testM}
import zio.test.Assertion._
import zio.test.mock.Expectation._
import zio.test._

object UserServiceSpec extends DefaultRunnableSpec {

  def spec =
    suite("User manager service")(
      testM("get user") {
        val env: ULayer[UserPersistence] =
          UserPersistenceMock.Retrieve(equalTo("What is your name?"), value(User.Test))

        val app = UserService.getUser(UserId.Test)

        val result = app.provideLayer(env ++ UserService.live)
        assertM(result)(equalTo(User.Test))
      }
    )

}
