package io.movies.algebras

import io.movies.model.{Movie, RegisteredMovie}

trait Repository[F[_]] {
  def addMovie(movie: Movie): F[RegisteredMovie]
  def getMovies: F[List[RegisteredMovie]]
  def getMovieById(id: Int): F[Option[RegisteredMovie]]
  def createMovieTable: F[Int]
  def dropMovieTable: F[Int]
}
