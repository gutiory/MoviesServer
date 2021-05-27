package io.movies.model

import java.time.Year

import doobie.Get
import doobie.Put

case class Movie(title: String, director: String, releaseDate: Year)

case class RegisteredMovie(
    id: Int,
    title: String,
    director: String,
    releaseDate: Year
)

object RegisteredMovie {
  def toInt(y: Year): Int   = y.getValue
  def fromInt(i: Int): Year = Year.of(i)

  implicit val yearGet: Get[Year] = Get[Int].tmap(fromInt)
  implicit val yearPut: Put[Year] = Put[Int].tcontramap(toInt)
}
