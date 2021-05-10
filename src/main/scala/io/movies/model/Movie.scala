package io.movies.model

case class Movie(title: String, director: String, releaseDate: Short)

case class RegisteredMovie(id: Short, title: String, director: String, releaseDate: Short)
