package io.movies

import cats.effect._
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger


import doobie._
import doobie.hikari._
import doobie.implicits._
import io.movies.services.MovieService
import io.movies.algebras.Repository
import io.movies.interpreters.RepositoryImpl
import io.movies.api.MovieServiceHttp4s

object Main extends IOApp {
  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO] // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver", // driver classname
        //"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // connect URL
        "jdbc:postgresql://127.0.0.1:5432/MovieService",
        "postgres", // username
        "somePassword", // password
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa

  val drop: Update0 =
    sql"""
         DROP TABLE IF EXISTS movie
    """.update

  val create: Update0 =
    sql"""
           CREATE TABLE movie ( id SERIAL, title VARCHAR NOT NULL,
           director VARCHAR NOT NULL UNIQUE, year SMALLINT )
    """.update

  println("-" * 100)

  override def run(args: List[String]): IO[ExitCode] = {
    transactor.use { xa =>
      for {
        implicit0(logger: SelfAwareStructuredLogger[IO]) <- Slf4jLogger.create[IO]
        repository: Repository[IO] = RepositoryImpl.doobie[IO](xa)
        implicit0(ms: MovieService[IO]) = MovieService.impl[IO](repository)
        api = new MovieServiceHttp4s[IO]
        _ <- api.stream(List())
      } yield ExitCode.Success
    }
  }
}
