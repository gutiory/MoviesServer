package io.movies.interpreters

import cats.effect._
import doobie._
import doobie.hikari._
import doobie.implicits._
import io.movies.algebras.Repository
import io.movies.model.{Movie, RegisteredMovie}
import org.typelevel.log4cats.SelfAwareStructuredLogger

object RepositoryImpl {

  //no se pasa el recurso para que no se crea/destruya cada vez. no es viable en el caso de conexiones a BBDD
  def doobie[F[_]](transactor: HikariTransactor[F])(implicit S: Sync[F], logger: SelfAwareStructuredLogger[F]) : F[Repository[F]] =
    S.pure(new Repository[F] {

    override def addMovie(movie: Movie): F[Int] = {
      val insert: Update0 = sql"insert into movie (title, director, year) values (${movie.title}, ${movie.director}, ${movie.releaseDate})".update
      logger.info(s"add movie $movie")
      insert.run.transact(transactor)
    }

    override def getMovies: F[List[RegisteredMovie]] = {
      val select = sql"select id, title, director, year from movie".query[RegisteredMovie].to[List]
      logger.info(s"get all movies with")
      select.transact(transactor)
    }

    override def getMovieById(id: Short): F[Option[RegisteredMovie]] = {
      val select = sql"select id, title, director, year from movie where id = $id".query[RegisteredMovie].option
      logger.info(s"get movie with id: $id")
      logger.debug(s"get movie with id: $id")
      select.transact(transactor)
    }
  }
  )
}
