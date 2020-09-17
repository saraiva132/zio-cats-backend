import sbt._

object Dependencies {

  private val testWithIt = "it, test"

  object ZIO {
    val zioVersion     = "1.0.1"
    val interopVersion = "2.1.4.0"
    val loggingVersion = "0.5.1"

    val zio          = "dev.zio" %% "zio"               % zioVersion
    val streams      = "dev.zio" %% "zio-streams"       % zioVersion
    val macros       = "dev.zio" %% "zio-macros"        % zioVersion
    val catsInterop  = "dev.zio" %% "zio-interop-cats"  % interopVersion
    val logging      = "dev.zio" %% "zio-logging"       % loggingVersion
    val loggingSlf4j = "dev.zio" %% "zio-logging-slf4j" % loggingVersion

    val all: Seq[ModuleID] = Seq(zio, streams, macros, catsInterop, logging, loggingSlf4j)
  }

  object Cats {
    private val catsV       = "2.2.0"
    private val catsEffectV = "2.2.0"

    val core   = "org.typelevel" %% "cats-core"   % catsV
    val effect = "org.typelevel" %% "cats-effect" % catsEffectV

    val all: Seq[ModuleID] = Seq(core, effect)
  }

  object Tapir {
    private val version = "0.16.16"

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

    private val version = "2.2.8"

    val zioClient   = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % version
    val clientCirce = "com.softwaremill.sttp.client" %% "circe"                         % version

    val all: Seq[ModuleID] = Seq(zioClient, clientCirce)
  }

  object Http4s {
    private val version          = "0.21.7"
    private val prometheusHtt4sV = "0.4.2"

    val server          = "org.http4s"        %% "http4s-blaze-server"       % version
    val dsl             = "org.http4s"        %% "http4s-dsl"                % version
    val client          = "org.http4s"        %% "http4s-blaze-client"       % version
    val circe           = "org.http4s"        %% "http4s-circe"              % version
    val prometheus      = "org.http4s"        %% "http4s-prometheus-metrics" % version
    val prometheusHtt4s = "io.chrisdavenport" %% "epimetheus-http4s"         % prometheusHtt4sV

    val all: Seq[ModuleID] = Seq(server, dsl, client, circe, prometheus, prometheusHtt4s)
  }

  object Doobie {
    private val version = "0.9.2"

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
    private val version = "2.4.4"

    val core = "co.fs2" %% "fs2-core" % version

    val all: Seq[ModuleID] = Seq(core)
  }

  object Enum {
    private val version = "1.6.1"

    val core  = "com.beachape" %% "enumeratum"       % version
    val circe = "com.beachape" %% "enumeratum-circe" % version

    val all: Seq[ModuleID] = Seq(core, circe)
  }

  object Flyway {
    val core   = "org.flywaydb"   % "flyway-core" % "6.5.6"
    val driver = "org.postgresql" % "postgresql"  % "42.2.16"

    val all: Seq[ModuleID] = Seq(core, driver)
  }

  object Config {
    private val version  = "0.13.0"
    private val refinedV = "0.9.15"

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
