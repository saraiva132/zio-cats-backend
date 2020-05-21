package zio.cats.backend

import zio.{App, UIO, ZIO, ZEnv}

object Main extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    UIO.succeed(0)
  }
}
