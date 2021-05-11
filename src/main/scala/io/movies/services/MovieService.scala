package io.movies.services

import io.movies.algebras.Repository
import io.movies.model.{Movie, RegisteredMovie}

trait MovieService[F[_]]{
  def getMovieById(id: Short): F[Option[RegisteredMovie]]
  def getMovies: F[List[RegisteredMovie]]
  def addMovie(movie: Movie): F[Int]
}

object MovieService{
  def impl[F[_]](implicit repository: Repository[F]) =
    new MovieService[F] {
      override def getMovieById(id: Short): F[Option[RegisteredMovie]] = {
        repository.getMovieById(id)
      }

      override def getMovies: F[List[RegisteredMovie]] = {
        repository.getMovies
      }

      override def addMovie(movie: Movie): F[Int] = {
        repository.addMovie(movie)
      }
    }
}

  // Con otra tabla de directores, se podría primero acceder a esa por nombre, coger id y usar
  // ese id en la tabla movie

