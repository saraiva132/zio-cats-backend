package zio.cats.backend.system.logging

import zio.ULayer
import zio.logging.{LogAnnotation, Logging}
import zio.logging.slf4j.Slf4jLogger

object Logger {

  private val logFormat = "[correlation-id = %s] %s"

  val live: ULayer[Logging] = Slf4jLogger.make { (context, message) =>
    val correlationId = LogAnnotation.CorrelationId.render(
      context.get(LogAnnotation.CorrelationId)
    )
    logFormat.format(correlationId, message)
  }
}
