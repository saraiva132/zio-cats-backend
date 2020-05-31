package zio.cats.backend

import cats.implicits._

import zio.ULayer
import zio.cats.backend.data.Error.{DatabaseError, EmailDoesNotMatch, ErrorFetchingUser, UserNotFound}
import zio.cats.backend.data.{Email, PostUser, User, UserId}
import zio.cats.backend.mocks.{ReqResClientMock, UserPersistenceMock}
import zio.cats.backend.persistence.UserPersistence
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.services.user.UserService
import zio.test.Assertion._
import zio.test.mock.Expectation._
import zio.test.{DefaultRunnableSpec, suite, testM, _}

object UserServiceSpec extends DefaultRunnableSpec {

  def spec =
    suite("User Service")(
      testM("create user success") {
        val mocks: ULayer[UserPersistence with ReqResClient] =
          ReqResClientMock.FetchUser(equalTo(UserId.Test), value(User.Test)) ++
              UserPersistenceMock.Create(equalTo(User.Test), value(User.Test))

        val result = UserService
          .createUser(PostUser.Test)
          .provideCustomLayer(mocks ++ UserService.live)

        assertM(result)(Assertion.isUnit)
      },
      testM("create user fails: userId not found in reqres") {
        val mocks: ULayer[UserPersistence with ReqResClient] =
          ReqResClientMock.FetchUser(equalTo(UserId.Test), failure(UserNotFound(UserId.Test))) ++
              UserPersistenceMock.Create(anything, never)

        val result = UserService
          .createUser(PostUser.Test)
          .provideCustomLayer(mocks ++ UserService.live)

        assertM(result.run)(fails(isSubtype[UserNotFound](anything)))
      },
      testM("create user fails: reqres returns error") {
        val mocks: ULayer[UserPersistence with ReqResClient] =
          ReqResClientMock.FetchUser(equalTo(UserId.Test), failure(ErrorFetchingUser(UserId.Test, "Fails"))) ++
              UserPersistenceMock.Create(anything, never)

        val result = UserService
          .createUser(PostUser.Test)
          .provideCustomLayer(mocks ++ UserService.live)

        assertM(result.run)(fails(isSubtype[ErrorFetchingUser](anything)))
      },
      testM("create user fails: email does not match") {
        val mocks: ULayer[UserPersistence with ReqResClient] =
          ReqResClientMock.FetchUser(equalTo(UserId.Test), value(User.Test)) ++
              UserPersistenceMock.Create(anything, never)

        val result = UserService
          .createUser(PostUser(UserId.Test, Email("badEmail@email")))
          .provideCustomLayer(mocks ++ UserService.live)

        assertM(result.run)(fails(isSubtype[EmailDoesNotMatch](anything)))
      },
      testM("create user fails: database error") {
        val mocks: ULayer[UserPersistence with ReqResClient] =
          ReqResClientMock.FetchUser(equalTo(UserId.Test), value(User.Test)) ++
              UserPersistenceMock.Create(equalTo(User.Test), failure(DatabaseError("Database error!")))

        val result = UserService
          .createUser(PostUser.Test)
          .provideCustomLayer(mocks ++ UserService.live)

        assertM(result.run)(fails(isSubtype[DatabaseError](anything)))
      },
      testM("get user success") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Retrieve(equalTo(UserId.Test), value(User.Test.some))

        val result = UserService
          .getUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result)(equalTo(User.Test))
      },
      testM("get user fails: not found") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Retrieve(equalTo(UserId.Test), value(None))

        val result = UserService
          .getUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result.run)(fails(isSubtype[UserNotFound](anything)))
      },
      testM("get user fails: database error") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Retrieve(equalTo(UserId.Test), failure(DatabaseError("Database error!")))

        val result = UserService
          .getUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result.run)(fails(isSubtype[DatabaseError](anything)))
      },
      testM("delete user success") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Delete(equalTo(UserId.Test), value(true))

        val result = UserService
          .deleteUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result)(Assertion.isUnit)
      },
      testM("delete user fails: not found") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Delete(equalTo(UserId.Test), value(false))

        val result = UserService
          .deleteUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result.run)(fails(isSubtype[UserNotFound](anything)))
      },
      testM("delete user fails: database error") {
        val mock: ULayer[UserPersistence] =
          UserPersistenceMock.Delete(equalTo(UserId.Test), failure(DatabaseError("Database error!")))

        val result = UserService
          .deleteUser(UserId.Test)
          .provideCustomLayer(mock ++ UserService.live)

        assertM(result.run)(fails(isSubtype[DatabaseError](anything)))
      }
    )
}
