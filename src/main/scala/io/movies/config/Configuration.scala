package io.movies.config

case class Configuration(
    url: String,
    user: String,
    password: String,
    dockerImage: String,
    driver: String
)
