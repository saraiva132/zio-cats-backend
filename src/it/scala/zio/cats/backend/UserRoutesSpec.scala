package zio.cats.backend

import eu.timepit.refined.types.string.NonEmptyString
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.basicRequest
import zio.{IO, ZIO}
import zio.cats.backend.data.{Email, PostUser, User, UserId}
import zio.test.{testM, _}
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

object UserRoutesSpec extends DefaultRunnableSpec {

  val loggerLayer     = Clock.live ++ Console.live >>> Logger.test
  val configLayer     = loggerLayer >>> Config.live
  val httpClientLayer = configLayer >>> Client.live
  val startUpLayer    = Clock.live ++ httpClientLayer >>> StartUpCleanUp.live

  val postUser = basicRequest
    .body(PostUser.Test)
    .post(uri"$usersEndpoint")

  def getUser(id: UserId) =
    basicRequest
      .response(asJson[User])
      .get(uri"$usersEndpoint/${id.value}")

  val deleteUser = basicRequest
    .delete(uri"$usersEndpoint/${UserId.Test.value}")

  val expected = User(
    id = UserId(1),
    email = Email("george.bluth@reqres.in"),
    first_name = NonEmptyString.unsafeFrom("George"),
    last_name = NonEmptyString.unsafeFrom("Bluth")
  )

  def spec =
    suite("User routes")(
      testM("allows creation of a new  user")(
        for {
          client <- ZIO.access[SttpClient](_.get)
          _      <- client.send(postUser)
          user <-
            client
              .send(getUser(UserId.Test))
              .map(_.body.toOption.get)
        } yield assert(user)(equalTo(expected))
      ),
      testM("allows deletion of existing user")(
        for {
          client <- ZIO.access[SttpClient](_.get)
          _      <- client.send(postUser)
          _      <- client.send(deleteUser)
          result <-
            client
              .send(getUser(UserId.Test))
              .map(_.body)
              .flatMap(IO.fromEither(_).mapError(_.body))
              .flip

        } yield assert(result)(equalTo("{\"message\":\"User with id 1 was not found!\"}"))
      ),
      testM("returns not found if user is not registered")(
        for {
          client <- ZIO.access[SttpClient](_.get)
          result <-
            client
              .send(getUser(UserId(0)))
              .map(_.body)
              .flatMap(IO.fromEither(_).mapError(_.body))
              .flip

        } yield assert(result)(equalTo("{\"message\":\"User with id 0 was not found!\"}"))
      ),
      testM("returns badRequest if post payload is not correct")(
        for {
          client <- ZIO.access[SttpClient](_.get)
          code <-
            client
              .send(
                basicRequest
                  .body(Map("id" -> "someId"))
                  .post(uri"$usersEndpoint")
              )
              .map(_.code)

        } yield assert(code)(equalTo(StatusCode.BadRequest))
      )
    ).provideCustomLayerShared((httpClientLayer ++ startUpLayer).orDie)
}
