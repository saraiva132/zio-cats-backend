package zio.cats.backend.http.routes

import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import sttp.tapir.ztapir.{ZEndpoint, endpoint}
import zio.cats.backend.data._
import zio.cats.backend.http.ClientError
import zio.cats.backend.persistence.UserPersistence
import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.cats.backend.services.user.UserService
import zio.interop.catz._
import zio.{Task, URIO}
import ClientError._
import sttp.model.StatusCode
import zio.logging.Logging

object UserRoutes {

  //API Definition
  private val postUser: ZEndpoint[PostUser, ClientError, Unit] =
    endpoint.post
      .in("users")
      .in(jsonBody[PostUser])
      .errorOut(
        oneOf(
          statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
          statusMapping(StatusCode.BadRequest, jsonBody[BadRequest])
        )
      )

  private val getUser: ZEndpoint[Int, ClientError, User] =
    endpoint.get
      .in("users" / path[Int]("email"))
      .out(jsonBody[User])
      .errorOut(
        oneOf(
          statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
          statusMapping(StatusCode.BadRequest, jsonBody[BadRequest])
        )
      )

  private val deleteUser: ZEndpoint[Int, ClientError, Unit] =
    endpoint.delete
      .in("users" / path[Int]("email"))
      .errorOut(
        oneOf(
          statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
          statusMapping(StatusCode.BadRequest, jsonBody[BadRequest])
        )
      )

  //Route implementation
  private val postUserRoute: URIO[UserService with UserPersistence with ReqResClient with Logging, HttpRoutes[Task]] =
    postUser
      .toRoutesR(postUser =>
        UserService
          .createUser(postUser)
          .mapToClientError
      )

  private val getUserRoute: URIO[UserService with UserPersistence with Logging, HttpRoutes[Task]] =
    getUser
      .toRoutesR(userId =>
        UserService
          .getUser(UserId(userId))
          .mapToClientError
      )

  private val deleteUserRoute: URIO[UserService with UserPersistence with Logging, HttpRoutes[Task]] =
    deleteUser
      .toRoutesR(userId =>
        UserService
          .deleteUser(UserId(userId))
          .mapToClientError
      )

  val routes: URIO[UserService with UserPersistence with ReqResClient with Logging, HttpRoutes[Task]] = for {
    postUserRoute   <- postUserRoute
    getUserRoute    <- getUserRoute
    deleteUserRoute <- deleteUserRoute
  } yield postUserRoute <+> getUserRoute <+> deleteUserRoute

  val docs = List(postUser, getUser, deleteUser).toOpenAPI("User manager", "0.1")

}
