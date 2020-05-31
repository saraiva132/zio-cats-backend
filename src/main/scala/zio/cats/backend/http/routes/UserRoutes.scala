package zio.cats.backend.http.routes

import cats.implicits._

import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir.{ZEndpoint, endpoint, statusMapping, _}

import zio.cats.backend.UserServiceEnv
import zio.cats.backend.data._
import zio.cats.backend.http.ClientError
import zio.cats.backend.http.ClientError._
import zio.cats.backend.services.user.UserService
import zio.interop.catz._
import zio.{Task, URIO}

object UserRoutes {

  val httpErrors = oneOf(
    statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    statusMapping(StatusCode.BadRequest, jsonBody[BadRequest]),
    statusMapping(StatusCode.NotFound, jsonBody[NotFound])
  )

  //API Definition
  private val postUser: ZEndpoint[PostUser, ClientError, Unit] =
    endpoint.post
      .in("users")
      .in(jsonBody[PostUser])
      .errorOut(httpErrors)

  private val getUser: ZEndpoint[Int, ClientError, User] =
    endpoint.get
      .in("users" / path[Int]("email"))
      .out(jsonBody[User])
      .errorOut(httpErrors)

  private val deleteUser: ZEndpoint[Int, ClientError, Unit] =
    endpoint.delete
      .in("users" / path[Int]("email"))
      .errorOut(httpErrors)

  //Route implementation
  private val postUserRoute: URIO[UserServiceEnv, HttpRoutes[Task]] =
    postUser
      .toRoutesR(postUser =>
        UserService
          .createUser(postUser)
          .mapToClientError
      )

  private val getUserRoute: URIO[UserServiceEnv, HttpRoutes[Task]] =
    getUser
      .toRoutesR(userId =>
        UserService
          .getUser(UserId(userId))
          .mapToClientError
      )

  private val deleteUserRoute: URIO[UserServiceEnv, HttpRoutes[Task]] =
    deleteUser
      .toRoutesR(userId =>
        UserService
          .deleteUser(UserId(userId))
          .mapToClientError
      )

  val routes: URIO[UserServiceEnv, HttpRoutes[Task]] = for {
    postUserRoute   <- postUserRoute
    getUserRoute    <- getUserRoute
    deleteUserRoute <- deleteUserRoute
  } yield postUserRoute <+> getUserRoute <+> deleteUserRoute

  val docs = List(postUser, getUser, deleteUser).toOpenAPI("User manager", "0.1")

}
