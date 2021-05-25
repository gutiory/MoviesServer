package io.movies.config

case class Port(number: Int) extends AnyVal

case class Configuration(
    url: String,
    user: String,
    password: String,
    dockerImage: String,
    driver: String
)
