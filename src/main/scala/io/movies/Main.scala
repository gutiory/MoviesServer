package io.movies

import cats.effect._
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import doobie._
import doobie.hikari._
import io.movies.services.MovieService
import io.movies.algebras.Repository
import io.movies.interpreters.RepositoryImpl
import io.movies.api.MovieServiceHttp4s
import io.movies.config.Configuration
import pureconfig._
import pureconfig.generic.auto._

object Main extends IOApp {

  def withConfig[C: ConfigReader](program: C => IO[ExitCode]): IO[ExitCode] =
    ConfigSource.default
      .load[C]
      .fold(
        error => IO.delay(println(error.prettyPrint())).as(ExitCode.Error),
        config => program(config)
      )

  def withConfig[C: ConfigReader](
      namespace: String
  )(program: C => IO[ExitCode]): IO[ExitCode] =
    ConfigSource.default
      .at(namespace)
      .load[C]
      .fold(
        error => IO.delay(println(error.prettyPrint())).as(ExitCode.Error),
        config => program(config)
      )

  override def run(args: List[String]): IO[ExitCode] = {
    withConfig[Configuration]("db") { config =>
      val transactor: Resource[IO, HikariTransactor[IO]] =
        for {
          ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
          be <- Blocker[IO] // our blocking EC
          xa <- HikariTransactor.newHikariTransactor[IO](
            config.driver,
            config.url,
            config.user,
            config.password,
            ce, // await connection here
            be // execute JDBC operations here
          )
        } yield xa

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
}
