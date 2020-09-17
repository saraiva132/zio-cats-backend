package zio.cats.backend

import eu.timepit.refined.types.string.NonEmptyString
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.basicRequest
import zio.{IO, ZIO}
import zio.cats.backend.data.{Email, PostUser, User, UserId}
import zio.test._
import sttp.client._
import sttp.client.circe._
import sttp.model.StatusCode
import zio.cats.backend.http.Client
import zio.cats.backend.system.config.Config
import zio.cats.backend.system.logging.Logger
import zio.clock.Clock
import zio.console.Console
import zio.test.Assertion._
import TestData._
import zio.blocking.Blocking
import zio.cats.backend.system.dbtransactor.DBTransactor
import io.circe.Error

object UserRoutesSpec extends DefaultRunnableSpec {

  type testDependencies = SttpClient with DBTransactor with Clock

  val loggerLayer            = Clock.live ++ Console.live >>> Logger.test
  val configLayer            = loggerLayer >>> Config.live
  val transactorLayer        = configLayer ++ Blocking.live ++ loggerLayer >>> DBTransactor.test
  val httpClientLayer        = configLayer >>> Client.live
  val beforeAllAfterAllLayer = Clock.live ++ httpClientLayer >>> BeforeAllAfterAll.live

  val beforeAfterLayer = BeforeAfter.live.orDie
  val sharedTestsLayer = (Clock.live ++ transactorLayer ++ httpClientLayer ++ beforeAllAfterAllLayer).orDie

  val expected = User(
    id = UserId(1),
    email = Email("george.bluth@reqres.in"),
    first_name = NonEmptyString.unsafeFrom("George"),
    last_name = NonEmptyString.unsafeFrom("Bluth")
  )

  def spec =
    suite("User routes")(
      testM("creation of new user: success") {
        val userToPost = PostUser.Test.copy(email = Email("george.bluth@reqres.in"))

        for {
          client    <- ZIO.service[SttpClientService]
          _         <- client.send(postUser(userToPost))
          response  <- client.send(getUser(UserId.Test))
          statusCode = response.code
          result     = response.body.toOption.get
        } yield assert(result)(equalTo(expected)) && assert(statusCode)(equalTo(StatusCode.Ok))

      },
      testM("creation of new user: bad email") {
        val userToPost    = PostUser.Test.copy(email = Email("someOther@email"))
        val expectedError = s"""{"message":"There is no user with email ${userToPost.email.value} for ${userToPost.user_id.value}."}"""

        for {
          client    <- ZIO.service[SttpClientService]
          response  <- client.send(postUser(userToPost))
          statusCode = response.code
          message   <- ZIO.fromEither(response.body).flip
        } yield assert(message)(equalTo(expectedError)) && assert(statusCode)(equalTo(StatusCode.NotFound))
      },
      testM("creation of new user: bad request")(
        for {
          client <- ZIO.service[SttpClientService]
          code <-
            client
              .send(
                basicRequest
                  .body(Map("id" -> "someId"))
                  .post(uri"$usersEndpoint")
              )
              .map(_.code)
        } yield assert(code)(equalTo(StatusCode.BadRequest))
      ),
      testM("creation of new user: userId not found") {
        val expected = """{"message":"User with id 0 was not found!"}"""

        for {
          client    <- ZIO.service[SttpClientService]
          response  <- client.send(getUser(UserId(0)))
          statusCode = response.code
          message   <- extractResponseErrorMessage(response)
        } yield assert(message)(equalTo(expected)) && assert(statusCode)(equalTo(StatusCode.NotFound))
      },
      testM("deletion of existing user success") {
        val userToPost    = PostUser.Test.copy(email = Email("george.bluth@reqres.in"))
        val expectedError = """{"message":"User with id 1 was not found!"}"""

        for {
          client    <- ZIO.service[SttpClientService]
          _         <- client.send(postUser(userToPost))
          user      <- client.send(getUser(UserId.Test)).map(_.body.toOption.get)
          _         <- client.send(deleteUser(UserId.Test))
          response  <- client.send(getUser(UserId.Test))
          statusCode = response.code
          message   <- extractResponseErrorMessage(response)
        } yield assert(user)(equalTo(expected)) &&
          assert(message)(equalTo(expectedError)) &&
          assert(statusCode)(equalTo(StatusCode.NotFound))
      }
    ).provideSomeLayer[testDependencies](beforeAfterLayer)
      .provideCustomLayerShared(sharedTestsLayer)

  def extractResponseErrorMessage[A](response: Response[Either[ResponseError[Error], A]]): IO[A, String] =
    ZIO
      .fromEither(response.body)
      .mapError {
        case HttpError(body, _) => body
        case _                  => ""
      }
      .flip

  def postUser(postUser: PostUser) =
    basicRequest
      .body(postUser)
      .post(uri"$usersEndpoint")

  def getUser(id: UserId) =
    basicRequest
      .response(asJson[User])
      .get(uri"$usersEndpoint/${id.value}")

  def deleteUser(userId: UserId) = basicRequest.delete(uri"$usersEndpoint/${userId.value}")

}
