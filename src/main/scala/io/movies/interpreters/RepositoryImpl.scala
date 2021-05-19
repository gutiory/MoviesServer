package io.movies.interpreters

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import io.movies.algebras.Repository
import io.movies.model.{Movie, RegisteredMovie}
import io.movies.model.RegisteredMovie._
import org.typelevel.log4cats.SelfAwareStructuredLogger

object RepositoryImpl {

  def doobie[F[_]](transactor: Transactor[F])(implicit S: Sync[F], logger: SelfAwareStructuredLogger[F]) : Repository[F] =
    new Repository[F] {
    override def addMovie(movie: Movie): F[RegisteredMovie] = {
      val insert: ConnectionIO[RegisteredMovie] =
        sql"""insert into movie (title, director, year)
              values (${movie.title}, ${movie.director}, ${movie.releaseDate.getValue})"""
          .update
          .withUniqueGeneratedKeys[RegisteredMovie]("id", "title", "director", "year")
        // *> Gets translated to expr1.flatMap(_ => expr2)
        logger.info(s"add movie $movie") *> insert.transact(transactor)
    }

    override def getMovies: F[List[RegisteredMovie]] = {
      val select = sql"select id, title, director, year from movie".query[RegisteredMovie].to[List]
      logger.info(s"get all movies") *> select.transact(transactor)
    }

    override def getMovieById(id: Int): F[Option[RegisteredMovie]] = {
      val select = sql"select id, title, director, year from movie where id = $id".query[RegisteredMovie].option
      logger.info(s"get movie with id: $id") *> select.transact(transactor)
    }

    override def createMovieTable: F[Int] = {
      val create: Update0 =
        sql"""
         CREATE TABLE movie ( id SERIAL, title VARCHAR NOT NULL,
         director VARCHAR NOT NULL, year SMALLINT )
        """.update
      create.run.transact(transactor)
    }

    override def dropMovieTable: F[Int] = {
      val drop: Update0 = sql"DROP TABLE IF EXISTS movie".update
      drop.run.transact(transactor)
    }
  }
}
