package zio.cats.backend.testkit

import scala.reflect.runtime.universe.TypeTag

import cats.effect.Effect
import cats.effect._

import doobie.implicits._
import doobie.util.query.{Query, Query0}
import doobie.util.testing._
import doobie.util.testing.{AnalysisArgs, Analyzable, CheckerBase}

import zio.interop.catz._
import zio.test.{Assertion, TestResult, assert}
import zio.{Task, ZEnv, Runtime}

trait ZIOChecker extends CheckerBase[Task] {

  implicit def runtime: Runtime[ZEnv] //Use test runtime
  def M: Effect[Task] = implicitly

  def check[A: Analyzable](a: A): Task[TestResult] = checkImpl(Analyzable.unpack(a))

  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  def checkOutput[A: TypeTag](q: Query0[A]) =
    checkImpl(
      AnalysisArgs(
        s"Query0[${typeName[A]}]",
        q.pos,
        q.sql,
        q.outputAnalysis
      )
    )

  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  def checkOutput[A: TypeTag, B: TypeTag](q: Query[A, B]) =
    checkImpl(
      AnalysisArgs(
        s"Query[${typeName[A]}, ${typeName[B]}]",
        q.pos,
        q.sql,
        q.outputAnalysis
      )
    )

  private def checkImpl(args: AnalysisArgs): Task[TestResult] =
    analyze(args).transact(transactor).map { report =>
      assert(report.succeeded)(Assertion.isTrue).map { details =>
        val text = formatReport(args, report, colors)
          .padLeft("  ")
          .toString
        details.label(text)
      }
    }

}
