package zio.cats.backend

import sttp.client.asynchttpclient.zio.SttpClient
import zio.{Has, UIO, ZIO, ZLayer}
import sttp.client._
import TestData.healthCheckEndpoint
import sttp.model.StatusCode
import zio.clock.Clock
import zio.duration._

/**
  * Experimenting with using layers to create beforeAll/afterAll behaviour
  */
object BeforeAllAfterAll {

  type dummyService = Has[Unit]

  private val healthCheck = basicRequest.get(uri"$healthCheckEndpoint")

  private val acquire: ZIO[SttpClient with Clock, Throwable, Unit] =
    for {
      client <- ZIO.service[SttpClientService]
      _      <- client.send(healthCheck).delay(100.millis).doUntil(_.code == StatusCode.Ok)
    } yield ()

  val live: ZLayer[SttpClient with Clock, Throwable, dummyService] =
    ZLayer.fromAcquireRelease(acquire)(_ => UIO.unit)

}
