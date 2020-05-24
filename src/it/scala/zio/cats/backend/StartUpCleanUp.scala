package zio.cats.backend

import sttp.client.asynchttpclient.zio.SttpClient
import zio.{Has, UIO, ZIO, ZLayer}
import sttp.client._
import TestData.healthCheckEndpoint
import sttp.model.StatusCode
import zio.clock.Clock
import zio.duration._

object StartUpCleanUp {

  type dummyService = Has[Unit]

  val healthCheck = basicRequest.get(uri"$healthCheckEndpoint")

  def acquire: ZIO[SttpClient with Clock, Throwable, Unit] =
    for {
      client <- ZIO.access[SttpClient](_.get)
      _      <- client.send(healthCheck).delay(100.millis).doUntil(_.code == StatusCode.Ok)
    } yield ()

  def live: ZLayer[SttpClient with Clock, Throwable, dummyService] =
    ZLayer.fromAcquireRelease(acquire)(_ => UIO.unit)

}
