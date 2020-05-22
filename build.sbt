import Dependencies._
import sbt._
import sbt.Package.ManifestAttributes

val alias: Seq[sbt.Def.Setting[_]] =
  addCommandAlias(
    "build",
    "all test scalafmtCheck packagedArtifacts publish docker:stage"
  ) ++ addCommandAlias("integrationTest", "it:test") ++
      addCommandAlias("prepare", "fmt; fix") ++
      addCommandAlias("fix", "all compile:scalafix test:scalafix") ++
      addCommandAlias("fixCheck", "; compile:scalafix --check ; test:scalafix --check") ++
      addCommandAlias("fmt", "all scalafmtSbt scalafmtAll") ++
      addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")

lazy val zioCatsBackend = project
  .in(file("."))
  .settings(thisBuildSettings)
  .settings(Compile / mainClass := Some("zio.cats.backend.Main"))
  .settings(alias)
  .settings(Defaults.itSettings)
  .configs(IntegrationTest extend Test)

lazy val thisBuildSettings = inThisBuild(
  Seq(
    scalaVersion := "2.13.1",
    name := "zio-cats-backend",
    normalizedName := "zio-cats-backend",
    description := "A backend service integrating ZIO with cats, http4s, doobie and tapir",
    startYear := Some(2020),
    Compile / packageDoc / publishArtifact := false,
    packageDoc / publishArtifact := false,
    publish / skip := true,
    Compile / doc / sources := Seq.empty,
    Compile / doc / javaOptions := Seq.empty,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    IntegrationTest / parallelExecution := false,
    packageOptions := Seq(
          ManifestAttributes(
            ("Implementation-Version", (ThisProject / version).value)
          )
        ),
    libraryDependencies ++= dependencies ++ plugins
  )
)

lazy val dependencies =
  Cats.all ++
      Http4s.all ++
      Config.all ++
      Streaming.all ++
      ZIO.all ++
      Tapir.all ++
      Doobie.all ++
      Enum.all ++
      Testing.all

lazy val plugins = Seq(
  compilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  compilerPlugin("io.tryp"        % "splain"             % "0.5.6" cross CrossVersion.patch),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  compilerPlugin(scalafixSemanticdb)
)

ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.3.1"
