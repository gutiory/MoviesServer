import Dependencies._

ThisBuild / scalaVersion     := "2.13.5"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "MoviesServer",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"          % "2.6.0",
      "org.typelevel" %% "cats-effect"        % "2.5.0",
      "org.tpolecat" %% "doobie-core"         % "0.13.2",
      "org.tpolecat" %% "doobie-postgres"     % "0.13.2",
      "org.tpolecat" %% "doobie-specs2"       % "0.13.2",
      "org.tpolecat" %% "doobie-hikari"       % "0.13.2",
      "org.http4s" %% "http4s-dsl"            % "0.21.22",
      "org.http4s" %% "http4s-blaze-server"   % "0.21.22",
      "org.http4s" %% "http4s-blaze-client"   % "0.21.22",
      "org.http4s" %% "http4s-circe"          % "0.21.22",
      "io.circe"   %% "circe-generic"         % "0.13.0",
      "io.circe" %% "circe-literal"           % "0.13.0",
      "org.gnieh"  %% "fs2-data-xml"          % "0.10.0",
      "org.typelevel" %% "log4cats-slf4j"     % "1.3.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
      "ch.qos.logback" % "logback-classic"    % "1.2.3",
      "com.github.pureconfig" %% "pureconfig" % "0.15.0",
      "org.postgresql" % "postgresql" % "42.1.1",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.3" % "test",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.3" % "test"
    )
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

enablePlugins(FlywayPlugin)
flywayDriver := "org.postgresql.Driver"
flywayUrl := "jdbc:postgresql://127.0.0.1:5432/MovieService"
flywayUser := "postgres"
flywayPassword := "somePassword"
flywayLocations += "filesystem:src/main/resources/db/migration"
flywaySchemas := Seq("schema1", "schema2", "schema3")
/*flywayPlaceholders := Map(
  "keyABC" -> "valueXYZ",
  "otherplaceholder" -> "value123"
)*/
