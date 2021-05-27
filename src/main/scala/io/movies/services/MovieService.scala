package io.movies.services

import io.movies.algebras.Repository
import io.movies.model.Movie
import io.movies.model.RegisteredMovie

trait MovieService[F[_]] {
  def getMovieById(id: Short): F[Option[RegisteredMovie]]
  def getMovies: F[List[RegisteredMovie]]
  def addMovie(movie: Movie): F[RegisteredMovie]
}

object MovieService {
  def impl[F[_]](implicit repository: Repository[F]): MovieService[F] =
    new MovieService[F] {
      override def getMovieById(id: Short): F[Option[RegisteredMovie]] = {
        repository.getMovieById(id)
      }

      override def getMovies: F[List[RegisteredMovie]] = {
        repository.getMovies
      }

      override def addMovie(movie: Movie): F[RegisteredMovie] = {
        repository.addMovie(movie)
      }
    }
}

// Use of this layer: with other table, directors for example, we could get the director id by name
// and use that id in the movie table instead of the name
