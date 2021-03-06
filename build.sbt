import Dependencies._
import com.typesafe.config.ConfigFactory

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"
ThisBuild / semanticdbEnabled := true                        // enable SemanticDB
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision // only required for Scala 2.x
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "MoviesServer",
    scalacOptions += "-Wunused:imports", // required by `RemoveUnused` rule
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"                       % "2.6.0",
      "org.typelevel"              %% "cats-effect"                     % "2.5.0",
      "org.tpolecat"               %% "doobie-core"                     % "0.13.2",
      "org.tpolecat"               %% "doobie-postgres"                 % "0.13.2",
      "org.tpolecat"               %% "doobie-specs2"                   % "0.13.2",
      "org.tpolecat"               %% "doobie-hikari"                   % "0.13.2",
      "org.http4s"                 %% "http4s-dsl"                      % "0.21.22",
      "org.http4s"                 %% "http4s-blaze-server"             % "0.21.22",
      "org.http4s"                 %% "http4s-blaze-client"             % "0.21.22",
      "org.http4s"                 %% "http4s-circe"                    % "0.21.22",
      "io.circe"                   %% "circe-generic"                   % "0.13.0",
      "io.circe"                   %% "circe-literal"                   % "0.13.0",
      "org.gnieh"                  %% "fs2-data-xml"                    % "0.10.0",
      "org.typelevel"              %% "log4cats-slf4j"                  % "1.3.0",
      "com.typesafe.scala-logging" %% "scala-logging"                   % "3.9.3",
      "ch.qos.logback"              % "logback-classic"                 % "1.2.3",
      "com.github.pureconfig"      %% "pureconfig"                      % "0.15.0",
      "org.postgresql"              % "postgresql"                      % "42.1.1",
      "com.dimafeng"               %% "testcontainers-scala-scalatest"  % "0.39.3" % "test",
      "com.dimafeng"               %% "testcontainers-scala-postgresql" % "0.39.3" % "test"
    )
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

lazy val databaseConfig = settingKey[com.typesafe.config.Config](
  "Typesafe config file with Flyway settings"
)

databaseConfig :=
  ConfigFactory
    .parseFile(
      (Compile / resourceDirectory).value / "application.conf"
    )
    .resolve()

enablePlugins(FlywayPlugin)
flywayDriver := databaseConfig.value.getString("db.driver")
flywayUrl := databaseConfig.value.getString("db.url")
flywayUser := databaseConfig.value.getString("db.user")
flywayPassword := databaseConfig.value.getString("db.password")

Compile / run := (Compile / run).dependsOn(flywayMigrate).evaluated
