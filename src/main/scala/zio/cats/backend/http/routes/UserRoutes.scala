package zio.cats.backend.http.routes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import sttp.tapir.json.circe._
import sttp.tapir.ztapir.{ZEndpoint, endpoint}
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.cats.backend.services.reqres.reqres.ReqRes
import zio.cats.backend.data._
import zio.cats.backend.http.routes.UserRoutes.ClientError
import zio.cats.backend.persistence.UserPersistenceSQL.UserPersistence
import zio.{Task, URIO}
import zio.cats.backend.services.user
import zio.cats.backend.services.user.UserManager
import zio.interop.catz._
import cats.implicits._

final class UserRoutes {

  private val postUser: ZEndpoint[PostUser, ClientError, Unit] =
    endpoint.post.in("users").in(jsonBody[PostUser]).errorOut(jsonBody[ClientError])

  private val getUser: ZEndpoint[String, ClientError, User] =
    endpoint.get.in("users" / path[String]("email")).errorOut(jsonBody[ClientError]).out(jsonBody[User])

  private val deleteUser: ZEndpoint[String, ClientError, Unit] =
    endpoint.delete.in("users" / path[String]("email")).errorOut(jsonBody[ClientError])

  private val postUserRoute: URIO[UserManager with UserPersistence with ReqRes, HttpRoutes[Task]] =
    postUser.toRoutesR(postUser => user.registerUser(postUser).mapError(err => ClientError(err.getMessage)))

  private val getUserRoute: URIO[UserManager with UserPersistence, HttpRoutes[Task]] =
    getUser.toRoutesR(userId => user.getUser(UserId(userId)).mapError(err => ClientError(err.getMessage)))

  private val deleteUserRoute: URIO[UserManager with UserPersistence, HttpRoutes[Task]] =
    deleteUser.toRoutesR(userId => user.deleteUser(UserId(userId)).mapError(err => ClientError(err.getMessage)))

  val routes: URIO[UserManager with UserPersistence with ReqRes, HttpRoutes[Task]] = for {
    postUserRoute   <- postUserRoute
    getUserRoute    <- getUserRoute
    deleteUserRoute <- deleteUserRoute
  } yield postUserRoute <+> getUserRoute <+> deleteUserRoute

}

object UserRoutes {
  final case class ClientError(message: String)

  object ClientError {
    implicit val codec: Codec[ClientError] = deriveCodec
  }
}
