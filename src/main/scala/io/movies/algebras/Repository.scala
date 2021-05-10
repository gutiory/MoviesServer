package io.movies.algebras

import io.movies.model.{Movie, RegisteredMovie}

trait Repository[F[_]] {
  def addMovie(movie: Movie): F[Int]
  //def getMovies: F[List[RegisteredMovie]]
  def getMovieById(id: Short): F[Option[RegisteredMovie]]
}
