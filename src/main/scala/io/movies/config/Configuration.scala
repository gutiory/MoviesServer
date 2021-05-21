package io.movies.config

case class Port(number: Int) extends AnyVal

case class Configuration(
    url: String,
    username: String,
    password: String,
    database: String,
    dockerImage: String,
    driverClassName: String
)
