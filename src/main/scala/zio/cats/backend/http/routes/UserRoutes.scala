package zio.cats.backend.http.routes

import cats.implicits._

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import sttp.tapir.ztapir.{ZEndpoint, endpoint}

import zio.cats.backend.data._
import zio.cats.backend.http.routes.UserRoutes.ClientError
import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence
import zio.cats.backend.services.reqres.reqres.ReqRes
import zio.cats.backend.services.user.UserService
import zio.interop.catz._
import zio.{Task, URIO}

final class UserRoutes {

  //API Definition
  private val postUser: ZEndpoint[PostUser, ClientError, Unit] =
    endpoint.post.in("users").in(jsonBody[PostUser]).errorOut(jsonBody[ClientError])

  private val getUser: ZEndpoint[Int, ClientError, User] =
    endpoint.get.in("users" / path[Int]("email")).errorOut(jsonBody[ClientError]).out(jsonBody[User])

  private val deleteUser: ZEndpoint[Int, ClientError, Unit] =
    endpoint.delete.in("users" / path[Int]("email")).errorOut(jsonBody[ClientError])

  //Route implementation
  private val postUserRoute: URIO[UserService with UserPersistence with ReqRes, HttpRoutes[Task]] =
    postUser.toRoutesR(postUser => UserService.registerUser(postUser).mapError(err => ClientError(err.getMessage)))

  private val getUserRoute: URIO[UserService with UserPersistence, HttpRoutes[Task]] =
    getUser.toRoutesR(userId => UserService.getUser(UserId(userId)).mapError(err => ClientError(err.getMessage)))

  private val deleteUserRoute: URIO[UserService with UserPersistence, HttpRoutes[Task]] =
    deleteUser.toRoutesR(userId => UserService.deleteUser(UserId(userId)).mapError(err => ClientError(err.getMessage)))

  val routes: URIO[UserService with UserPersistence with ReqRes, HttpRoutes[Task]] = for {
    postUserRoute   <- postUserRoute
    getUserRoute    <- getUserRoute
    deleteUserRoute <- deleteUserRoute
  } yield postUserRoute <+> getUserRoute <+> deleteUserRoute

  val docs = List(postUser, getUser, deleteUser).toOpenAPI("User manager", "0.1")

}

object UserRoutes {
  final case class ClientError(message: String)

  object ClientError {
    implicit val codec: Codec[ClientError] = deriveCodec
  }
}
