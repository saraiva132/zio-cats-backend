import sbt._

object Dependencies {

  private val testWithIt = "it, test"

  object ZIO {
    val zioVersion     = "1.0.0-RC19-2"
    val interopVersion = "2.0.0.0-RC14"
    val loggingVersion = "0.2.9"

    val zio          = "dev.zio" %% "zio"               % zioVersion
    val streams      = "dev.zio" %% "zio-streams"       % zioVersion
    val macros       = "dev.zio" %% "zio-macros"        % zioVersion
    val catsInterop  = "dev.zio" %% "zio-interop-cats"  % interopVersion
    val logging      = "dev.zio" %% "zio-logging"       % loggingVersion
    val loggingSlf4j = "dev.zio" %% "zio-logging-slf4j" % loggingVersion

    val all: Seq[ModuleID] = Seq(zio, streams, macros, catsInterop, logging, loggingSlf4j)
  }

  object Cats {
    private val catsV       = "2.1.1"
    private val catsEffectV = "2.1.3"
    private val catsMtlV    = "0.7.0"

    val core          = "org.typelevel" %% "cats-core"        % catsV
    val free          = "org.typelevel" %% "cats-free"        % catsV
    val jvm           = "org.typelevel" %% "cats-jvm"         % catsV
    val `kernel-laws` = "org.typelevel" %% "cats-kernel-laws" % catsV
    val kernel        = "org.typelevel" %% "cats-kernel"      % catsV
    val laws          = "org.typelevel" %% "cats-laws"        % catsV
    val macros        = "org.typelevel" %% "cats-macros"      % catsV
    val effect        = "org.typelevel" %% "cats-effect"      % catsEffectV
    val mtl           = "org.typelevel" %% "cats-mtl-core"    % catsMtlV

    val all: Seq[ModuleID] = Seq(core, jvm, kernel, macros, effect)
  }

  object Tapir {
    private val version = "0.15.2"

    val core             = "com.softwaremill.sttp.tapir" %% "tapir-core"               % version
    val docs             = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % version
    val openApiCirce     = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe"      % version
    val openApiCirceYaml = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % version
    val sttp             = "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % version
    val http4s           = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % version
    val zio              = "com.softwaremill.sttp.tapir" %% "tapir-zio"                % version
    val zioHttp4sServer  = "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % version
    val circe            = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % version
    val swagger          = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % version

    val all: Seq[ModuleID] = Seq(core, docs, openApiCirce, openApiCirceYaml, sttp, http4s, zio, zioHttp4sServer, circe, swagger)
  }

  object STTP {

    private val version = "2.1.4"

    val zioClient   = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % version
    val clientCirce = "com.softwaremill.sttp.client" %% "circe"                         % version

    val all: Seq[ModuleID] = Seq(zioClient, clientCirce)
  }

  object Http4s {
    private val version          = "0.21.4"
    private val prometheusHtt4sV = "0.4.0"

    val server          = "org.http4s"        %% "http4s-blaze-server"       % version
    val dsl             = "org.http4s"        %% "http4s-dsl"                % version
    val client          = "org.http4s"        %% "http4s-blaze-client"       % version
    val circe           = "org.http4s"        %% "http4s-circe"              % version
    val prometheus      = "org.http4s"        %% "http4s-prometheus-metrics" % version
    val prometheusHtt4s = "io.chrisdavenport" %% "epimetheus-http4s"         % prometheusHtt4sV

    val all: Seq[ModuleID] = Seq(server, dsl, client, circe, prometheus, prometheusHtt4s)
  }

  object Doobie {
    private val version = "0.9.0"

    val core     = "org.tpolecat" %% "doobie-core"     % version
    val hikari   = "org.tpolecat" %% "doobie-hikari"   % version
    val postgres = "org.tpolecat" %% "doobie-postgres" % version
    val refined  = "org.tpolecat" %% "doobie-refined"  % version

    val all: Seq[ModuleID] = Seq(core, hikari, refined, postgres)
  }

  object Circe {
    private val version       = "0.13.0"
    private val versionExtras = "0.13.0"

    val core             = "io.circe" %% "circe-core"           % version
    val generic          = "io.circe" %% "circe-generic"        % version
    val parser           = "io.circe" %% "circe-parser"         % version
    val refined          = "io.circe" %% "circe-refined"        % version
    val `generic-extras` = "io.circe" %% "circe-generic-extras" % versionExtras

    val all: Seq[ModuleID] = Seq(core, generic, parser, refined, `generic-extras`)
  }

  object Streaming {
    private val version = "2.3.0"

    val core     = "co.fs2" %% "fs2-core"             % version
    val io       = "co.fs2" %% "fs2-io"               % version
    val reactive = "co.fs2" %% "fs2-reactive-streams" % version

    val all: Seq[ModuleID] = Seq(core, io, reactive)
  }

  object Enum {
    private val version = "1.6.1"

    val core  = "com.beachape" %% "enumeratum"       % version
    val circe = "com.beachape" %% "enumeratum-circe" % version

    val all: Seq[ModuleID] = Seq(core, circe)
  }

  object Flyway {
    val core   = "org.flywaydb"   % "flyway-core" % "6.4.2"
    val driver = "org.postgresql" % "postgresql"  % "42.2.12"

    val all: Seq[ModuleID] = Seq(core, driver)
  }

  object Config {
    private val version  = "0.12.3"
    private val refinedV = "0.9.14"

    val pureconfig        = "com.github.pureconfig" %% "pureconfig"         % version
    val pureconfigRefined = "eu.timepit"            %% "refined-pureconfig" % refinedV

    val all: Seq[ModuleID] = Seq(pureconfig, pureconfigRefined)
  }

  object Logging {
    private val logbackV = "1.2.3"

    val Core               = "ch.qos.logback" % "logback-core"    % logbackV
    val Logback            = "ch.qos.logback" % "logback-classic" % logbackV
    val all: Seq[ModuleID] = Seq(Core, Logback)
  }

  object Testing {
    val zioTest    = "dev.zio" %% "zio-test"     % ZIO.zioVersion % testWithIt
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % ZIO.zioVersion % testWithIt

    val all: Seq[ModuleID] = Seq(zioTest, zioTestSbt)
  }

}
